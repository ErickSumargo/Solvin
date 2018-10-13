package id.solvin.dev.model.basic;


import java.io.Serializable;
import java.util.List;

/**
 * Created by edinofri on 29/10/2016.
 */

public class Pertanyaan implements Serializable {
    private int id;
    private int user_id;
    private int materi_id;
    private int status;
    private int kelas;
    private int pelajaran;
    private String image;
    private String content;
    private String created_at;
    private String updated_at;

    private User penanya;
    private Material materi;
    private List<Jawaban> jawaban;
    private List<Komentar> komentar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getKelas() {
        return kelas;
    }

    public void setKelas(int kelas) {
        this.kelas = kelas;
    }

    public int getPelajaran() {
        return pelajaran;
    }

    public void setPelajaran(int pelajaran) {
        this.pelajaran = pelajaran;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getMateri_id() {
        return materi_id;
    }

    public void setMateri_id(int materi_id) {
        this.materi_id = materi_id;
    }

    public Material getMateri() {
        return materi;
    }

    public void setMateri(Material materi) {
        this.materi = materi;
    }

    public List<Jawaban> getJawaban() {
        return jawaban;
    }

    public void setJawaban(List<Jawaban> jawaban) {
        this.jawaban = jawaban;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public User getPenanya() {
        return penanya;
    }

    public void setPenanya(User penanya) {
        this.penanya = penanya;
    }

    public List<Komentar> getKomentar() {
        return komentar;
    }

    public void setKomentar(List<Komentar> komentar) {
        this.komentar = komentar;
    }
}
