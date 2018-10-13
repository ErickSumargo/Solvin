<?php

namespace App\Http\Controllers;

use App\Classes\BaseConfig;
use App\Models\Balance;
use App\Models\Comment;
use App\Models\Material;
use App\Models\Mentor;
use App\Models\Notification;
use App\Models\Question;
use App\Models\Solution;
use App\Models\Student;
use App\Models\Subject;
use Illuminate\Http\Request;

use App\Classes\Firebase;
use App\Http\Requests;

class QuestionController extends Controller
{
    use BaseConfig;

    private $my_mentor_id;

    #param(*material_id,*status,*auth_id,*auth_type)
    public function index(Request $req, $last_id = 0)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($last_id == 0) {
            $last_id = Question::max('id') + 1;
        }

        $material_id = $req['material_id'] == null || empty($req['material_id']) ? null : $req['material_id'];
        $status = $req['status'] < 0 ? null : $req['status'];
        /*
         * users who visited
         */
        $auth_id = empty($req['auth_id']) || $req['auth_id'] == null ? null : $req['auth_id'];
        $auth_type = empty($req['auth_type']) || $req['auth_type'] == null ? null : $req['auth_type'];

        $mode = $this->getType();

        $mentor_id = $auth_type == "mentor" ? $auth_id : null;
        $auth_id = $mentor_id != null ? null : $auth_id;
        $this->my_mentor_id = $mentor_id;
        if ($this->my_mentor_id != null) {
            $question = Question::with(
                array(
                    'Material' => function ($query) {
                        $query->select(['id', 'name', 'subject_id']);
                    },
                    'Material.Subject' => function ($query) {
                        $query->select(['id', 'name']);
                    },
                    'Student' => function ($query) {
                        $query->select($this->simpleAuth);
                    },
                    'Comments' => function ($query) {
                        $query->select('*');
                    },
                    'Solutions' => function ($query) {
                        $query->select($this->table_solution . ".*");
                    },
                    'Solutions.Mentor' => function ($query) {
                        $query->select($this->simpleAuth);
                    }
                )
            )
                ->where([
                    [$this->table_question . '.id', '<', $last_id],
                    [[$material_id == null ? ['material_id', '>', '-1'] : ['material_id', '=', $material_id],
                        $status == null ? ['status', '>', '-1'] : ['status', '=', $status],
                        $auth_id == null ? ['student_id', '>', '-1'] : ['student_id', '=', $auth_id],
                    ]]
                ])
                ->join($this->table_solution, $this->table_question . '.id', '=', $this->table_solution . '.question_id')
                ->where([$this->my_mentor_id == null ? ['mentor_id', '>', '-1'] : ['mentor_id', '=', $this->my_mentor_id]])
                ->select($this->table_question . ".*", $this->table_solution . ".question_id")
                ->orderBy('created_at', 'desc')
                ->limit($this->item_perpage)
                ->get();
        } else {
            $question = Question::with(
                array(
                    'Material' => function ($query) {
                        $query->select(['id', 'name', 'subject_id']);
                    },
                    'Material.Subject' => function ($query) {
                        $query->select(['id', 'name']);
                    },
                    'Student' => function ($query) {
                        $query->select($this->simpleAuth);
                    },
                    'Comments' => function ($query) {
                        $query->select('content');
                    },
                    'Solutions' => function ($query) {
                        $query->select($this->table_solution . ".*");
                    },
                    'Solutions.Mentor' => function ($query) {
                        $query->select($this->simpleAuth);
                    }
                )
            )
                ->where([
                    [$this->table_question . '.id', '<', $last_id],
                    [[$material_id == null ? ['material_id', '>', -1] : ['material_id', '=', $material_id],
                        $status == null ? ['status', '>', -1] : ['status', '=', $status],
                        $auth_id == null ? ['student_id', '>', -1] : ['student_id', '=', $auth_id],
                    ]]
                ])
                ->orderBy('created_at', 'desc')
                ->limit($this->item_perpage)
                ->get();
        }
        $user_status = false;
        foreach ($question as $q) {
            foreach ($q->solutions as $s) {
                $solution = Solution::where([
                    ['mentor_id', '=', $s->mentor->id]
                ])->get(['best']);
                if ($solution == null) {
                    $s->mentor->best_count = $this->scrypt->encrypt(0);
                    $s->mentor->solution_count = $this->scrypt->encrypt(0);
                } else {
                    $best = 0;
                    foreach ($solution as $sol) {
                        if ($sol->best) {
                            $best++;
                        }
                    }
                    $s->mentor->best_count = $this->scrypt->encrypt($best);
                    $s->mentor->solution_count = $this->scrypt->encrypt(count($solution));
                }
                $s->mentor->auth_type = 'mentor';
            }
            foreach ($q->comments as $k) {
                $k->auth = $k->auth_type == "student" ? Student::find($k->auth_id, $this->simpleAuth) : Mentor::find($k->auth_id, $this->simpleAuth);
            }
            unset($q->student->phone);
            unset($q->student->address);
            unset($q->student->birth);
            unset($q->student->school);
            unset($q->student->member_code);
            unset($q->student->credit);
            unset($q->student->credit_timelife);
            unset($q->student->created_at);
            unset($q->student->updated_at);

            if ($q->status == 1 && $mode == "student" && $auth_type == "mentor") {
                if ($this->auth->id == $q->student->id) {
                    $user_status = true;
                }
            } else if ($q->status == 1 && $mode == "mentor") {
                foreach ($q->solutions as $s) {
                    if ($s->mentor->id == $this->auth->id) {
                        $user_status = true;
                        break;
                    }
                }
            } else if ($q->status < 2 && $mode == "student") {
                if ($this->auth->id == $q->student->id) {
                    $user_status = true;
                }
            }
        }

        $related = false;
        if ($user_status) {
            $related = true;
        } else if ($mode == "student" && $auth_type == "student") {
            if ($this->auth->id == $auth_id) {
                $related = Question::where([['student_id', '=', $this->auth->id], ['status', '<', 2]])->count() > 0 ? true : false;
            }
        } else {
            $related = false;
        }
        $max_id = Question::max('id');
        $_data = array(
            'questions' => $question,
            'max_id' => $this->scrypt->encrypt($max_id == null ? 0 : $max_id),
            'related' => $related
        );

        $this->json_response['data'] = $_data;
        return $this->json_result();
    }

    #param (question_id)
    public function detail(Request $req)
    {
        $question_id = $req['question_id'];
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $question = Question::find($question_id);
        if ($question == null) {
            $this->json_response['message'] = 'pertanyaan tidak ditemukan';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $question = Question::with(
            array(
                'Material' => function ($query) {
                    $query->select(['id', 'name', 'subject_id']);
                },
                'Material.Subject' => function ($query) {
                    $query->select(['id', 'name']);
                },
                'Student' => function ($query) {
                    $query->select($this->simpleAuth);
                },
                'Comments' => function ($query) {
                    $query->select('*');
                },
                'Solutions' => function ($query) {
                    $query->select("*");
                },
                'Solutions.Mentor' => function ($query) {
                    $query->select($this->simpleAuth);
                }
            )
        )
            ->where('id', '=', $question_id)->first();
        foreach ($question->comments as $c) {
            $c->auth = $c->auth_type == "student" ? Student::find($c->auth_id, $this->simpleAuth) : Mentor::find($c->auth_id, $this->simpleAuth);
        }
        foreach ($question->solutions as $s) {
            $solution = Solution::where([
                ['mentor_id', '=', $s->mentor->id]
            ])->get(['best']);
            $best = 0;
            foreach ($solution as $sol) {
                if ($sol->best) {
                    $best++;
                }
            }
            $s->mentor->best_count = $best;
            $s->mentor->auth_type = "mentor";
            $s->mentor->solution_count = count($solution);
        }
        $_data = array(
            'question' => $question
        );
        $this->json_response['data'] = $_data;
        return $this->json_result();
    }

    #param(content,material_id,*other,*image)
    public function createQuestion(Request $req)
    {
        if (!$this->isStudent()) {
            return $this->json_result();
        }
        $content = $req['content'];
        $material = Material::find($req['material_id']);
        if ($material == null) {
            $this->json_response['message'] = 'material not found';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($this->auth->credit > 0) {
            if (!empty($content)) {
                $question = new Question();
                $question->student_id = $this->auth->id;
                $question->material_id = $material->id;
                $question->content = $content;
                if (isset($req['other']) && !empty($req['other'])) {
                    $question->other = $req['other'];
                }
                /**
                 * check & upload image
                 */
                if ($req->file('image') != null && !empty($req->file("image")->getClientOriginalName())) {
                    if ($this->isImageValid($req->file('image'))) {
                        $question->image = $this->getImageName($req->file('image'), 'question', $material->id);
                    } else {
                        return $this->json_result();
                    }
                }
                /**
                 * decrease credit
                 */
                $this->auth->credit -= 1;
                /**
                 * save to database
                 */
                $question->save();

                $this->auth->save();
                /**
                 * push notification
                 */
                $this->pushNotificationCreateQuestion($question, $material);

            } else {
                $this->json_response['message'] = 'Content tidak boleh kosong';
                $this->json_response['success'] = false;
            }
        } else {
            $this->json_response['message'] = 'Anda tidak memiliki credit pertanyaan';
            $this->json_response['success'] = false;
        }
        return $this->json_result();
    }

    #param(question_id,content,material_id,change_image,*image)
    public function editQuestion(Request $req)
    {
        if (!$this->isStudent()) {
            return $this->json_result();
        }
        $question = Question::where([
            ['id', '=', $req['question_id']],
            ['student_id', '=', $this->auth->id]
        ])->first();
        if ($question == null) {
            $this->json_response['message'] = 'Data pertanyaan atau murid tidak ditemukan';
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        if ($question->status >= 1) {
            $this->json_response['success'] = false;
            $this->json_response['error'][] = [
                'message' => 'Maaf, pertanyaan anda telah terjawab sehingga kegiatan pengeditan tidak dapat dilakukan lagi',
                'type' => $this->ERROR_EDIT_QUESTION
            ];
            return $this->json_result();
        }

        $material = Material::find($req['material_id']);
        if ($material == null) {
            $this->json_response['message'] = 'Material not found';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $question->content = $req['content'];
        $question->material_id = $material->id;
        $question->other = $req['other'];

        $change_image = $req['change_image'];
        if ($change_image == 'true') {
            $tmp_file = $question->image;
            /**
             * check & upload image / change image
             */
            if ($req->file('image') != null && !empty($req->file("image")->getClientOriginalName())) {
                if ($this->isImageValid($req->file('image'))) {
                    $question->image = $this->getImageName($req->file('image'), 'question', $material->id);
                } else {
                    return $this->json_result();
                }
            } else {
                $question->image = null;
            }
            $this->deleteImage($tmp_file, 'question');
        }
        /**
         * save to database
         */
        $question->save();
        return $this->json_result();
    }

    #param(question_id,solution_id)
    public function chooseTheBest(Request $req)
    {
        if (!$this->isStudent()) {
            return $this->json_result();
        }
        $question = Question::find($this->scrypt->decrypt($req['question_id']));
        if ($question == null) {
            $this->json_response['message'] = 'pertanyaan tidak ditemukan';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if ($question->status == 2) {
            $this->json_response['message'] = 'hanya bisa memilih sekali';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $solution = Solution::find($this->scrypt->decrypt($req['solution_id']));
        if ($solution == null) {
            $this->json_response['message'] = 'solusi tidak ditemukan';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $balance = new Balance();
        $balance->solution_id = $solution->id;
        $balance->deal_payment = $this->deal_payment;
        $balance->save();

        $question->status = 2;
        $solution->best = true;
        $question->save();
        $solution->save();
        /*
         * push notification
         */
        $this->pushNotificationChooseTheBest($question, $solution);
        return $this->json_result();
    }

    #param (question_id,content,*image)
    public function createSolution(Request $req)
    {
        if (!$this->isMentor()) {
            $this->json_result();
        }
        $question = Question::where([
            ['id', '=', $req['question_id']],
            ['status', '<', 2]
        ])->first();

        if ($question == null) {
            $this->json_response['message'] = 'pertanyaan tidak ditemukan';
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        $count_solution = Solution::where([
            [$this->table_solution . '.question_id', '=', $question->id]
        ])->count();

        if ($count_solution >= $this->max_solution) {
            $this->json_response['error'][] = [
                'message' => 'Maaf, solusi tidak diterima karena jumlah solusi yang masuk untuk pertanyaan ini telah mencapai batas maksimalnya',
                'type' => $this->ERROR_CREATE_SOLUTION
            ];
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $isDuplicate = Solution::where([
            ['mentor_id', '=', $this->auth->id],
            ['question_id', '=', $question->id]
        ])->first() == null ? false : true;

        if ($isDuplicate) {
            $this->json_response['message'] = 'Solusi duplikat';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (!empty($req['content'])) {
            $solution = new Solution();
            $solution->mentor_id = $this->auth->id;
            $solution->question_id = $question->id;
            $solution->content = $req['content'];
            /**
             * check & upload image
             */
            if ($req->file('image') != null && !empty($req->file("image")->getClientOriginalName())) {
                if ($this->isImageValid($req->file('image'))) {
                    $solution->image = $this->getImageName($req->file('image'), 'solution', $question->id);
                } else {
                    return $this->json_result();
                }
            }
            $question->status = 1;
            $this->pushNotificationCreateSolution($question, $solution);
            $solution->save();
            $question->save();
        } else {
            $this->json_response['message'] = 'Content tidak boleh kosong';
            $this->json_response['success'] = false;
        }
        return $this->json_result();
    }

    #param (question_id,solution_id,change_image,content,*image)
    public function editSolution(Request $req)
    {
        $question_id = $req['question_id'];
        $solution_id = $req['solution_id'];
        $change_image = $req['change_image'];
        $content = $req['content'];

        $question = Question::where([
            ['id', '=', $question_id],
            ['status', '<', 2]
        ])->first();
        if ($question == null) {
            $this->json_response['message'] = 'pertanyaan tidak ditemukan';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $solution = Solution::find($solution_id);
        if ($solution == null) {
            $this->json_response['message'] = 'solusi tidak ditemukan';
            $this->json_response['success'] = false;
        } else {
            if ($change_image == 'true') {
                $tmp_file = $solution->image;
                /**
                 * check & upload image / change image
                 */
                if ($req->file('image') != null && !empty($req->file("image")->getClientOriginalName())) {
                    if ($this->isImageValid($req->file('image'))) {
                        $solution->image = $this->getImageName($req->file('image'), 'solution', $question->id);
                    } else {
                        return $this->json_result();
                    }
                } else {
                    $solution->image = null;
                }
                $this->deleteImage($tmp_file, 'solution');
            }
            /**
             * save to database
             */
            $solution->content = $content;
            $solution->save();
        }
        return $this->json_result();
    }

    #param (question_id,content,auth_notif[],*image)
    public function createComment(Request $req)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $question = Question::find($req['question_id']);
        if ($question == null) {
            $this->json_response['message'] = 'pertanyaan tidak ditemukan';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        if (!empty($req['content'])) {
            $comment = new Comment();
            $comment->auth_id = $this->auth->id;
            $comment->question_id = $question->id;
            $comment->auth_type = $this->getType();
            $comment->content = $req['content'];
            /**
             * check & upload image
             */
            if ($req->file('image') != null && !empty($req->file("image")->getClientOriginalName())) {
                if ($this->isImageValid($req->file('image'))) {
                    $comment->image = $this->getImageName($req->file('image'), 'comment', $question->id);
                } else {
                    return $this->json_result();
                }
            }
            /*
             * send to any user
             */

            if (!empty($req['auth_notif'])) {
                $arrayAuthNotify = explode(',', $req['auth_notif']);
                foreach ($arrayAuthNotify as $authNotify) {
                    $authType = explode('.', $authNotify)[0];
                    $authId = explode('.', $authNotify)[1];

                    /**
                     * don't send to your self
                     */
                    if ($authType == $this->getType() && $authId == $this->auth->id) {
                        continue;
                    }
                    if (in_array($authType, ['student', 'mentor'])) {
                        $_auth = $authType == 'student' ? Student::find($authId) : Mentor::find($authId);

                        $this->pushNotificationCreateComment($question, $_auth, $authType);

                    } else {

                        $this->json_response['message'] = 'auth type not defined';
                        $this->json_response['success'] = false;
                        return $this->json_result();

                    }
                }
            }
            $comment->save();
        } else {
            $this->json_response['message'] = 'content can\'t be empty';
            $this->json_response['success'] = false;
        }
        return $this->json_result();
    }

    #param (question_id,comment_id,change_image,content,*image)
    public function editComment(Request $req)
    {
        if ($this->isGuest()) {
            $this->json_response['message'] = 'Unauthorized';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $question = Question::find($req['question_id']);
        if ($question == null) {
            $this->json_response['message'] = 'pertanyaan tidak ditemukan';
            $this->json_response['success'] = false;
            return $this->json_result();
        }
        $comment = Comment::find($req['comment_id']);
        $change_image = $req['change_image'];
        if ($comment == null) {
            $this->json_response['message'] = 'komentar tidak ditemukan';
            $this->json_response['success'] = false;
            return $this->json_result();
        } else {
            if ($change_image == "true") {
                $tmp_file = $comment->image;
                /**
                 * check & upload image / change image
                 */
                if ($req->file('image') != null && !empty($req->file("image")->getClientOriginalName())) {
                    if ($this->isImageValid($req->file('image'))) {
                        $comment->image = $this->getImageName($req->file('image'), 'comment', $question->id);
                    } else {
                        return $this->json_result();
                    }
                } else {
                    $comment->image = null;
                }
                $this->deleteImage($tmp_file, 'comment');

            }

            $comment->content = $req['content'];
            $comment->save();
        }
        return $this->json_result();
    }

    public function actionNotification(Request $req)
    {
        if (isset($req['notication_id'])) {
            $notification_id = $req['notication_id'];
        } else {
            return $this->json_result();
        }
        $notification = Notification::find($notification_id);
        if ($notification != null) {
            $notification->status = 1;
            $notification->save();
        } else {

        }
    }

    # notif | question created
    private function pushNotificationCreateQuestion($question, $material)
    {
        $mentors = Mentor::whereNotNull('firebase_token')->get();
        $subject = Subject::find($material->subject_id);
        $_content = Student::find($question->student_id)->name . ' mengajukan pertanyaan ' . strtolower($subject->name) . ' terbaru';

        foreach ($mentors as $mentor) {
            Firebase::sendPushNotification([
                /**
                 * type = jenis notifikasi (cth. Anda mendapatkan notifikasi Jawaban)
                 * auth_id = id penerima notifikasi
                 * auth_type = jenis penerima notifikasi
                 *
                 * sender_id = id pengirim
                 * sender_type = jenis pengirim
                 *
                 * subject_id = id item notifikasi
                 * subject_type = id type item notifikasi
                 *
                 * content = datanya
                 */
                "type" => "question",
                "auth_id" => $mentor->id,
                "auth_type" => "mentor",
                "sender_id" => $this->auth->id,
                "sender_type" => 'student',
                "subject_id" => $question->id,
                "subject_type" => "question",
                "content" => '%s mengajukan pertanyaan ' . strtolower($subject->name) . ' terbaru'], $_content, 1);
        }
    }

    # notif | choose the best
    private function pushNotificationChooseTheBest($question, $solution)
    {
        $_content = Student::find($question->student_id)->name . " menandai solusi anda sebagai yang terbaik";

        Firebase::sendPushNotification([
            /**
             * type = jenis notifikasi (cth. Anda mendapatkan notifikasi Jawaban)
             * auth_id = id penerima notifikasi
             * auth_type = jenis penerima notifikasi
             *
             * sender_id = id pengirim
             * sender_type = jenis pengirim
             *
             * subject_id = id item notifikasi
             * subject_type = id type item notifikasi
             *
             * content = datanya
             */
            "type" => "solution",
            "auth_id" => $solution->mentor_id,
            "auth_type" => "mentor",
            "sender_id" => $question->student_id,
            "sender_type" => 'student',
            "subject_id" => $question->id,
            "subject_type" => "question",
            "content" => "%s menandai solusi anda sebagai yang terbaik"], $_content);
    }

    # notif | solution created
    private function pushNotificationCreateSolution($question, $solution)
    {
        $_content = Mentor::find($solution->mentor_id)->name . ' mengajukan solusi atas pertanyaan anda';

        Firebase::sendPushNotification([
            /**
             * type = jenis notifikasi (cth. Anda mendapatkan notifikasi Jawaban)
             * auth_id = id penerima notifikasi
             * auth_type = jenis penerima notifikasi
             *
             * sender_id = id pengirim
             * sender_type = jenis pengirim
             *
             * subject_id = id item notifikasi
             * subject_type = id type item notifikasi
             *
             * content = datanya
             */
            "type" => "solution",
            "auth_id" => $question->student_id,
            "auth_type" => "student",
            "sender_id" => $solution->mentor_id,
            "sender_type" => 'mentor',
            "subject_id" => $question->id,
            "subject_type" => "question",
            "content" => '%s mengajukan solusi atas pertanyaan anda'], $_content, 1);
    }

    private function pushNotificationCreateComment($question, $_auth, $authType)
    {
        $subject = $authType == 'student' ? 'pertanyaan' : 'solusi';
        $_content = $_auth->name . " menyampaikan komentar terkait " . $subject . " yang anda ajukan";

        Firebase::sendPushNotification([
            /**
             * type = jenis notifikasi (cth. Anda mendapatkan notifikasi Jawaban)
             * auth_id = id penerima notifikasi
             * auth_type = jenis penerima notifikasi
             *
             * sender_id = id pengirim
             * sender_type = jenis pengirim
             *
             * subject_id = id item notifikasi
             * subject_type = id type item notifikasi
             *
             * content = datanya
             */
            "type" => "comment",
            "auth_id" => $_auth->id,
            "auth_type" => $authType,
            "sender_id" => $this->auth->id,
            "sender_type" => $this->getType(),
            "subject_id" => $question->id,
            "subject_type" => "question",
            "content" => "%s menyampaikan komentar terkait " . $subject . " yang anda ajukan"], $_content);
    }
}