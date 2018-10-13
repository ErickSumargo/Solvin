package id.solvin.dev.model.response;

import java.util.List;

import id.solvin.dev.model.basic.Bank;
import id.solvin.dev.model.basic.MobileNetwork;
import id.solvin.dev.model.basic.Paket;
import id.solvin.dev.model.basic.Response;
import id.solvin.dev.model.basic.Subject;
import id.solvin.dev.model.basic.Version;

/**
 * Created by edinofri on 02/12/2016.
 */

public class ResponseFirst extends Response {
    private ResultData data;

    public ResultData getData() {
        return data;
    }

    public class ResultData {
        private List<Bank> banks;
        private List<MobileNetwork> mobileNetworks;
        private List<Paket> packages;
        private List<Subject> materials;
        private Version version;

        public Version getVersion() {
            return version;
        }

        public List<Bank> getBanks() {
            return banks;
        }

        public List<MobileNetwork> getMobileNetworks() {
            return mobileNetworks;
        }

        public List<Paket> getPackages() {
            return packages;
        }

        public List<Subject> getMaterials() {
            return materials;
        }
    }
}
