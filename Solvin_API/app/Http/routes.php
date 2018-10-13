<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the controller to call when that URI is requested.
|
*/

Route::get('/', function () {
    return view('welcome');
});

Route::group(['prefix'=>'api'],function() {
    Route::group(['prefix' => 'app', 'middleware' => 'check.token.validate'], function () {
        Route::post('basic', 'ApplicationController@getLatestVersion');
    });

    Route::group(['prefix' => 'auth'], function () {
        Route::group(['prefix' => 'register'], function () {
            Route::post('step-1', 'AuthController@registerStepOne');
            Route::post('step-2', 'AuthController@registerStepTwo');
            Route::post('step-3', 'AuthController@registerStepThree');
        });
        Route::post('login', 'AuthController@login');
        Route::post('resetpasswordStepOne', 'AuthController@resetPasswordStepOne');
        Route::post('resetpasswordStepTwo', 'AuthController@resetPasswordStepTwo');
        Route::post('resetpasswordStepThree', 'AuthController@resetPasswordStepThree');
        Route::group(['middleware' => 'check.token.validate'], function () {
            Route::get('/profile/{auth}/{id}', 'AuthController@profile');
            Route::post('/profile/update', 'AuthController@update');
            Route::post('/profile/changephoto', 'AuthController@changePhoto');
            Route::post('/profile/changepassword', 'AuthController@changePassword');
            Route::get('/notification', 'AuthController@notification');
            Route::get('/notification/read', 'AuthController@actionNotification');
            Route::get('/notification/count', 'AuthController@countNotification');
            Route::get('/search', 'AuthController@search');
            Route::post('/feedback', 'AuthController@sendFeedback');
        });
    });

    Route::group(['middleware' => 'check.token.validate', 'prefix' => 'transaction'], function () {
        Route::group(['prefix' => 'purchase'], function () {
            Route::post('/', 'TransactionController@purchase');
            Route::post('confirm', 'TransactionController@confirmPurchase');
            Route::post('accept', 'TransactionController@acceptPurchase');
            Route::post('check', 'TransactionController@checkLastPurchase');
            Route::post('action', 'TransactionController@actionPurchase');
            Route::get('detail', 'TransactionController@detailPurchase');
            Route::get('history/{num}', 'TransactionController@historyPurchase');
        });
        Route::group(['prefix' => 'redeem'], function () {
            Route::post('/', 'TransactionController@redeem');
            Route::post('accept', 'TransactionController@acceptRedeem');
            Route::get('history/{num}', 'TransactionController@historyRedeem');
            Route::get('summary', 'TransactionController@summaryRedeem');
            Route::get('monthly/balance/{num}', 'TransactionController@monthlyBalance');
        });
    });

    Route::group(['middleware' => 'check.token.validate', 'prefix' => 'question'], function () {
        Route::get('/page/{num}', 'QuestionController@index');
        Route::get('/detail', 'QuestionController@detail');

        Route::post('create', 'QuestionController@createQuestion');
        Route::post('edit', 'QuestionController@editQuestion');

        Route::post('solution/best', 'QuestionController@chooseTheBest');
        Route::post('solution/create', 'QuestionController@createSolution');
        Route::post('solution/edit', 'QuestionController@editSolution');

        Route::post('comment/create', 'QuestionController@createComment');
        Route::post('comment/edit', 'QuestionController@editComment');
    });
});

//ADMIN PANEL
Route::group(['prefix' => 'admin_panel'], function () {
    Route::get('primary', 'APPrimary@loadPrimaryData');

    Route::post('login', 'APPrimary@login');

    Route::group(['middleware' => 'check.token.validate'], function () {
        Route::get('report', 'APReport@getMonthlyReport');
    });

    Route::group(['middleware' => 'check.token.validate', 'prefix' => 'main'], function () {
        Route::get('/', 'APMain@getCumulativeRecord');

        Route::group(['prefix' => 'home'], function () {
            Route::get('search', 'APMain@loadAuthList');

            Route::get('student/{num}', 'APMain@loadStudentList');
            Route::get('mentor/{num}', 'APMain@loadMentorList');

            Route::group(['middleware' => 'check.token.validate'], function () {
                Route::post('mentor/register', 'APMain@registerMentor');
            });

            Route::get('notification', 'APMain@loadNotificationList');
            Route::get('notification/count', 'APMain@getNotificationCount');
            Route::post('notification/read', 'APMain@setNotificationReadStatus');
        });

        Route::group(['prefix' => 'priority'], function () {
            Route::get('question/discuss/{num}', 'APMain@loadPriorityQuestionList');
            Route::get('question/detail', 'APMain@loadPriorityQuestionDetail');
            Route::post('question/detail/vote', 'APMain@voteBestSolution');

            Route::get('transaction/{num}', 'APMain@loadPriorityTransactionList');
            Route::post('transaction/action', 'APMain@setTransactionAction');

            Route::get('redeem_balance/{num}', 'APMain@loadPriorityRedeemBalanceList');
            Route::post('redeem_balance/action', 'APMain@setRedeemBalanceAction');
        });

        Route::group(['prefix' => 'feedback'], function () {
            Route::get('{num}', 'APMain@loadFeedbackList');
            Route::post('read', 'APMain@setFeedbackReadStatus');
        });
    });

    Route::group(['middleware' => 'check.token.validate', 'prefix' => 'detail'], function () {
        Route::get('student/profile', 'APDetail@loadStudentProfile');

        Route::get('student/question/{num}', 'APDetail@loadQuestionList');
        Route::post('student/question/edit', 'APDetail@editQuestion');
        Route::post('student/question/block', 'APDetail@blockQuestion');

        Route::get('student/transaction/{num}', 'APDetail@loadTransactionList');

        Route::get('mentor/profile', 'APDetail@loadMentorProfile');
        Route::post('mentor/change_photo', 'APDetail@changePhoto');

        Route::get('mentor/solution/{num}', 'APDetail@loadSolutionList');
        Route::post('mentor/solution/edit', 'APDetail@editSolution');
        Route::post('mentor/solution/block', 'APDetail@blockSolution');

        Route::get('mentor/redeem_balance/{num}', 'APDetail@loadRedeemBalanceList');
        Route::get('mentor/balance_bonus/{num}', 'APDetail@loadBalanceBonusList');

        Route::get('auth/comment/{num}', 'APDetail@loadCommentList');
        Route::post('auth/comment/edit', 'APDetail@editComment');
        Route::post('auth/comment/block', 'APDetail@blockComment');

        Route::post('auth/block', 'APDetail@blockAuth');
    });

    Route::get('test', 'APMain@test');
});