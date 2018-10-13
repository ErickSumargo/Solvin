<?php

namespace App\Http\Controllers;

use App\Classes\BaseConfig;
use App\Models\Bank;
use App\Models\MobileNetwork;
use App\Models\Material;
use App\Models\Package;
use App\Models\Subject;
use Illuminate\Http\Request;

use App\Http\Requests;

class ApplicationController extends Controller
{
    use BaseConfig;

    public function getLatestVersion(Request $req)
    {
        if (!$this->isAuthorization()) {
            return $this->json_result();
        }

        if (!isset($req['version_material']) ||
            !isset($req['version_package']) || !isset($req['version_bank'])
        ) {
            $this->json_response['message'] = 'required field don\'t exits';
            $this->json_response['success'] = false;
            return $this->json_result();
        }

        $resultData = new \stdClass();
        $resultVersionData = new \stdClass();
        if ($req['version_material'] != $this->getLatestVersionMaterial()) {
            $resultData->materials = $this->getMaterials();
        }
        if ($req['version_package'] != $this->getLatestVersionPackage()) {
            $resultData->packages = $this->getPackages();
        }
        if ($req['version_bank'] != $this->getLatestVersionBank()) {
            $resultData->banks = $this->getBanks();
        }
        if ($req['version_mobile_network'] != $this->getLatestVersionMobileNetwork()) {
            $resultData->mobileNetworks = $this->getMobileNetworks();
        }

        $resultVersionData->materials = $this->getLatestVersionMaterial();
        $resultVersionData->packages = $this->getLatestVersionPackage();
        $resultVersionData->banks = $this->getLatestVersionBank();
        $resultVersionData->mobileNetworks = $this->getLatestVersionMobileNetwork();
        $resultData->version = $resultVersionData;
        $this->json_response['data'] = $resultData;
        return $this->json_result();

    }

    private function getMaterials()
    {
        $subject = Subject::all(['id', 'name']);
        foreach ($subject as $s) {
            $s->material = Material::where([
                ['subject_id', '=', $s->id],
            ])->get(['id', 'name']);
        }
        return $subject;
    }

    private function getPackages()
    {
        return Package::all(['id', 'nominal', 'active', 'credit']);
    }

    private function getBanks()
    {
        return Bank::all();
    }

    private function getMobileNetworks()
    {
        return MobileNetwork::all();
    }
}