<?php
/**
 * Created by PhpStorm.
 * User: Erick Sumargo
 * Date: 3/22/2017
 * Time: 8:27 PM
 */

namespace App\Classes;

class SCrypt
{
    private $iv = '9i7t64OpS9WE9y25';
    private $key = 'PgK3RR2nAQ5wC5lD';

    function __construct()
    {
    }

    function encrypt($plain)
    {
        $iv = $this->iv;
        $td = mcrypt_module_open('rijndael-128', '', 'cbc', $iv);

        mcrypt_generic_init($td, $this->key, $iv);
        $encrypted = mcrypt_generic($td, $plain);

        mcrypt_generic_deinit($td);
        mcrypt_module_close($td);

        return bin2hex($encrypted);
    }

    function decrypt($encrypted)
    {
        $code = $this->hex2bin($encrypted);
        $iv = $this->iv;
        $td = mcrypt_module_open('rijndael-128', '', 'cbc', $iv);

        mcrypt_generic_init($td, $this->key, $iv);
        $decrypted = mdecrypt_generic($td, $code);

        mcrypt_generic_deinit($td);
        mcrypt_module_close($td);

        return utf8_encode(trim($decrypted));
    }

    protected function hex2bin($hex)
    {
        $bin = '';
        for ($i = 0; $i < strlen($hex); $i += 2) {
            $bin .= chr(hexdec(substr($hex, $i, 2)));
        }
        return $bin;
    }
}