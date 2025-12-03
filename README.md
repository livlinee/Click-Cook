# Click n Cook 🍳

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue.svg)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue.svg)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com/)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-yellow.svg)](https://firebase.google.com/)

Aplikasi Android berbasis komunitas untuk berbagi dan menemukan resep masakan. Click n Cook memungkinkan pengguna untuk menjelajahi berbagai kategori masakan, membuat dan membagikan resep mereka sendiri, memberikan ulasan, serta menyimpan resep favorit. Dilengkapi dengan panel Admin untuk moderasi konten dan pengguna.

---

## 📱 Fitur Utama

### 🧑‍🍳 Pengguna (User)

#### Autentikasi
- **Login & Register** - Sistem autentikasi menggunakan Firebase Auth (`LoginActivity.java`)
- **Lupa Password** - Reset password melalui email
- **Verifikasi Email** - Keamanan akun dengan verifikasi email

#### Eksplorasi Resep
- **Pencarian Resep** - Cari resep berdasarkan kata kunci (`RecipeListActivity.java`)
- **Filter Kategori** - Filter cepat berdasarkan kategori di halaman utama (`HomeFragment.java`)
- **Detail Resep** - Lihat detail lengkap resep, bahan-bahan, dan langkah memasak (`DetailRecipeActivity.java`)

#### Manajemen Resep
- **Membuat Resep** - Buat resep baru dengan foto, bahan-bahan, dan langkah memasak (`AddRecipeActivity.java`)
- **Draf & Terbitkan** - Simpan resep sebagai Draf atau langsung Terbitkan
- **Menu Manajemen** - Tombol opsi manajemen (Edit/Hapus) tersedia di halaman Profil (`menu_profile_options.xml`)
  - *Catatan: Fitur edit/hapus resep yang sudah diterbitkan masih dalam pengembangan*

#### Interaksi Sosial
- **Rating & Ulasan** - Berikan rating dan tulis ulasan untuk resep (`WriteReviewActivity.java`)
- **Favorit** - Simpan resep favorit untuk akses cepat (`FavoriteFragment.java`)

#### Profil & Pelaporan
- **Edit Profil** - Ubah data profil, nama, dan bio (`SettingsActivity.java`)
- **Laporkan Konten** - Laporkan resep yang melanggar aturan melalui bottom sheet dialog (`DetailRecipeActivity.java`)

### 👨‍💼 Administrator (Admin)

#### Dashboard
- **Statistik Real-time** - Total pengguna, resep terbit, dan laporan pending (`AdminHomeFragment.java`)

#### Manajemen Pengguna
- **Daftar Pengguna** - Lihat semua pengguna terdaftar (`AdminUsersFragment.java`)
- **Pencarian** - Cari pengguna berdasarkan nama/email
- **Moderasi** - Blokir/buka blokir pengguna

#### Manajemen Konten
- **Daftar Resep** - Lihat semua resep termasuk Draf (`AdminContentFragment.java`)
- **Hapus Konten** - Hapus resep yang melanggar aturan

#### Sistem Laporan
- **Review Laporan** - Tinjau laporan masuk (Pending/Resolved) (`AdminReportsFragment.java`)
- **Aksi Moderasi** - Hapus konten yang dilaporkan atau tolak laporan

---

## 🛠️ Teknologi & Library

### Core Technology
- **Bahasa**: Java
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36
- **IDE**: Android Studio

### Backend & Services
- **Firebase Authentication** - Manajemen autentikasi pengguna
- **Firebase Firestore** - Database NoSQL real-time
- **Firebase Storage** - Penyimpanan gambar (foto profil & resep)

### Third-Party Libraries
| Library | Versi | Fungsi |
|---------|-------|--------|
| [Glide](https://github.com/bumptech/glide) | Latest | Memuat dan caching gambar |
| [CircleImageView](https://github.com/hdodenhof/CircleImageView) | Latest | Menampilkan foto profil bulat |
| [Material Components](https://github.com/material-components/material-components-android) | Latest | Komponen UI modern |

---

## 📂 Struktur Project
```
com.example.clickncook
├── controllers/              # Logika Activity dan Fragment
│   ├── admin/               # Fitur Admin
│   │   ├── AdminHomeFragment.java
│   │   ├── AdminUsersFragment.java
│   │   ├── AdminContentFragment.java
│   │   └── AdminReportsFragment.java
│   ├── auth/                # Autentikasi
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   └── SplashActivity.java
│   └── user/                # Fitur User
│       ├── HomeFragment.java
│       ├── DetailRecipeActivity.java
│       ├── AddRecipeActivity.java
│       ├── RecipeListActivity.java
│       ├── WriteReviewActivity.java
│       ├── FavoriteFragment.java
│       └── SettingsActivity.java
├── models/                   # Data Model
│   ├── User.java
│   ├── Recipe.java
│   ├── Review.java
│   ├── Report.java
│   └── Bookmark.java
└── views/
    └── adapter/             # RecyclerView Adapters
        ├── RecipeAdapter.java
        └── [Other Adapters]

```
## 🚀 Cara Menjalankan Project

### Prerequisites
- Android Studio (versi terbaru)
- JDK 8 atau lebih tinggi
- Akun Firebase (untuk konfigurasi backend)

### Langkah Instalasi

1. **Clone Repository**
   git clone https://github.com/livlinee/Click-Cook.git
   cd click-n-cook

2. **Buka di Android Studio**
   - Buka Android Studio
   - Pilih `File > Open`
   - Arahkan ke folder project yang sudah di-clone

3. **Konfigurasi Firebase**
   - Buat project baru di [Firebase Console](https://console.firebase.google.com/)
   - Download file `google-services.json`
   - Letakkan file tersebut di folder `app/`
   - Setup Firebase Services:
     - **Authentication**: Enable Email/Password sign-in method
     - **Firestore Database**: Buat database dalam mode production
     - **Storage**: Setup storage bucket untuk upload gambar

4. **Firestore Database Structure**
   
   Buat collections berikut di Firestore:
   ```
   users/
   ├── {userId}
   │   ├── name: string
   │   ├── email: string
   │   ├── bio: string
   │   ├── photoUrl: string
   │   ├── isAdmin: boolean
   │   └── isBlocked: boolean
   
   recipes/
   ├── {recipeId}
   │   ├── title: string
   │   ├── description: string
   │   ├── imageUrl: string
   │   ├── ingredients: array
   │   ├── steps: array
   │   ├── category: string
   │   ├── authorId: string
   │   ├── status: string (draft/published)
   │   ├── createdAt: timestamp
   │   └── rating: number
   
   reviews/
   ├── {reviewId}
   │   ├── recipeId: string
   │   ├── userId: string
   │   ├── rating: number
   │   ├── comment: string
   │   └── createdAt: timestamp
   
   reports/
   ├── {reportId}
   │   ├── recipeId: string
   │   ├── reporterId: string
   │   ├── reason: string
   │   ├── status: string (pending/resolved)
   │   └── createdAt: timestamp
   
   bookmarks/
   ├── {bookmarkId}
   │   ├── userId: string
   │   ├── recipeId: string
   │   └── createdAt: timestamp
   ```
5. **Sync Gradle**
   - Pastikan semua dependency telah diunduh
   - Klik `Sync Now` jika diminta

6. **Run Application**
   - Hubungkan device Android atau jalankan emulator
   - Klik tombol `Run` (▶️) di Android Studio

---

## 📝 Status Pengembangan

### ✅ Fitur yang Sudah Diimplementasi
- ✅ Autentikasi pengguna (Login, Register, Reset Password)
- ✅ Eksplorasi dan pencarian resep
- ✅ Membuat resep baru (dengan Draf/Terbit)
- ✅ Rating dan review resep
- ✅ Bookmark/Favorit resep
- ✅ Dashboard admin dengan statistik
- ✅ Manajemen pengguna (blokir/unblok)
- ✅ Sistem pelaporan konten
- ✅ Edit profil pengguna

## 📄 License

Project ini dibuat untuk keperluan akademis dan pembelajaran.

---

## 👥 Tim Pengembang

**Click n Cook Team**
- Project Manager : Lila Vimala_F52123001
- Firebase Specialist : Astiawati Manda_F52123007
- UI/UX Frontend : Nur Istiqama_F52123005
- MVC + Tester : Aditya Zaldy_F52123027

*Dikembangkan dengan ❤️ menggunakan Java & Firebase*
