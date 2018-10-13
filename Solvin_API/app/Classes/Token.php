<?php
/**
 * Created by PhpStorm.
 * User: edinofri
 * Date: 29/01/2017
 * Time: 17:13
 */

namespace App\Classes;

use App\Classes\JWT\JWT;
use App\Models\Mentor;
use App\Models\Student;
use App\Models\Admin;

class Token
{

    private $secret;
    private $algo;

    private static $data;
    private static $type;
    private $error;
    private $valid;

    function __construct()
    {
        $this->secret = "M9dBdNRx2WXO8hPTe7Shp7yebOrulbrc1Jz9jm76";
        $this->algo = "HS256";
    }

    public function getToken($obj)
    {
        return JWT::encode($obj, $this->secret, $this->algo);
    }

    public function validate($authtype, $token)
    {
        # Solvin Student {Token}
        try {
            $_data = JWT::decode($token, $this->secret, array($this->algo));
            switch ($authtype) {
                case 'student':
                    self::$data = Student::where([
                        ['id', '=', $_data->id],
                        ['email', '=', $_data->email],
                        ['active', '=', 1]
                    ])->first();
                    break;
                case 'mentor':
                    self::$data = Mentor::where([
                        ['id', '=', $_data->id],
                        ['email', '=', $_data->email],
                        ['active', '=', 1]
                    ])->first();
                    break;
                case 'admin':
                    self::$data = Admin::where([
                        ['id', '=', $_data->id],
                        ['email', '=', $_data->email],
                        ['active', '=', 1]
                    ])->first();
                    break;
                case 'guest':
                    self::$data = $_data;
                    break;
                default:
                    break;
            }
            self::$type = $authtype;
            $this->valid = true;
            if ($authtype == 'student' || $authtype == 'mentor' || $authtype == 'admin') {
                if (self::$data == null) {
                    $this->error = $authtype . ' not found / banned';
                    $this->valid = false;
                }
            }
            return $this;

        } catch (\UnexpectedValueException      $e) {
            $this->error = $e->getMessage();

        } catch (\SignatureInvalidException     $e) {
            $this->error = $e->getMessage();

        } catch (\BeforeValidException          $e) {
            $this->error = $e->getMessage();

        } catch (\BeforeValidException          $e) {
            $this->error = $e->getMessage();

        } catch (\ExpiredException $e) {
            $this->error = $e->getMessage();
        } catch (\DomainException $e) {
            $this->error = $e->getMessage();
        } catch (\Exception $e) {
            $this->error = $e->getMessage();
        }
        if (empty($this->error)) {
            $this->error = 'out of exception ';
        }
        $this->valid = false;
        return $this;
    }

    public function isValid()
    {
        return $this->valid;
    }

    public function getAuth()
    {
        return self::$data;
    }

    public function getError()
    {
        return $this->error;
    }

    public function setError($message)
    {
        $this->error = $message;
    }

    public function getType()
    {
        return self::$type;
    }
}