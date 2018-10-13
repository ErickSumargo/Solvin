<?php

namespace App\Http\Middleware;

use App\Classes\Logging;
use App\Classes\Token;
use Closure;
use App\Classes\JWT\JWT;
use Request;

class CheckTokenValidate
{
    private $token;
    private $helper;

    public function handle($request, Closure $next)
    {
        $this->token = new Token();
        if (!$this->isHeaderValid(Request::header('Authorization'))) {

            if ($this->token->getError() == null) {
                $this->token->setError('Token invalid format');
            }
            $_response = array(
                'success' => false,
                'message' => $this->token->getError()
            );
            return response($_response, 200);
        }
        return $next($request);
    }

    private function isHeaderValid($header)
    {
        $headers = explode(' ', $header);
        if (count($headers) < 3) {
            return false;
        }
        $header_name = $headers[0];
        $header_auth = $headers[1];
        $header_token = $headers[2];

        if ($header_name != "Solvin" && $header_name != "SolvinAP") {
            return false;
        }
        if ($header_auth == 'student' || $header_auth == 'mentor' || $header_auth == 'admin' || $header_auth == 'guest') {
            return $this->token->validate($header_auth, $header_token)->isValid();
        }
        return false;
    }
}