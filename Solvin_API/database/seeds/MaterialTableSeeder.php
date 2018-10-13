<?php

use Illuminate\Database\Seeder;

class MaterialTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    use \App\Classes\BaseConfig;

    public function run()
    {
        $material_names = array(
            #--------------------------------
            # Matematika
            #--------------------------------
            'Aljabar',
            'Aritmatika Sosial',
            'Bangun Datar',
            'Bangun Ruang',
            'Barisan & Deret',
            'Bidang Koordinat',
            'Bilangan',
            'Dimensi Tiga',
            'Eksponen',
            'Fungsi',
            'Garis Segitiga',
            'Garis Sejajar',
            'Garis Singgung Lingkaran',
            'Himpunan',
            'Integral',
            'Kesebangunan & Kekongruenan',
            'Komposisi & Invers Fungsi',
            'Limit Fungsi',
            'Lingkaran',
            'Logaritma',
            'Logika Matematika',
            'Matriks',
            'Peluang',
            'Pengukuran',
            'Perbandingan',
            'Persamaan Garis Lurus',
            'Persamaan Kuadrat',
            'Persamaan Linear',
            'Pertidaksamaan',
            'Program Linear',
            'Statistika',
            'Sudut',
            'Sukubanyak (Polinomial)',
            'Teorema Pythagoras',
            'Transformasi Geometri',
            'Trigonometri',
            'Turunan',
            'Vektor',
            'Lainnya',
            #-------------------------------
            # Fisika
            #-------------------------------
            'Alat Optik',
            'Besaran & Satuan',
            'Bunyi',
            'Cahaya',
            'Dinamika Rotasi',
            'Elastisitas',
            'Fisika Atom',
            'Fisika Inti',
            'Gerak Harmonik Sederhana',
            'Gerak Lurus',
            'Gerak Melingkar',
            'Getaran & Gelombang',
            'Hukum Newton',
            'Impuls',
            'Induksi Elektromagnetik',
            'Kalor',
            'Keseimbangan Benda Tegar',
            'Listrik Dinamis',
            'Listrik Statis',
            'Medan Magnetik',
            'Mekanika Fluida',
            'Momentum',
            'Pemuaian',
            'Pengukuran',
            'Radiasi Benda Hitam',
            'Sistem Tata Surya',
            'Suhu',
            'Tekanan',
            'Teori Kinetik Gas',
            'Teori Kuantum',
            'Teori Relativitas Khusus',
            'Termodinamika',
            'Usaha & Energi',
            'Vektor',
            'Zat & Wujudnya',
            'Lainnya',
        );

        for ($i = 0; $i < count($material_names); $i++) {
            DB::table($this->table_material)->insert([
                'name' => $material_names[$i],
                'subject_id' => $i < 39 ? 1 : 2,
                'created_at' => date('Y-m-d H:i:s'),
                'updated_at' => date('Y-m-d H:i:s')
            ]);
        }
    }
}