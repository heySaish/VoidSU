# VoidSU

A powerful, kernel-assisted root management system and Jetpack Compose Android control center, tightly coupled with **VoidKernel** and **VoidFS**.

---

## 🚀 Overview

VoidSU is designed specifically for **VoidKernel**, providing zero-bloat root privilege orchestration, per-app profile isolation, and seamless capability integration with **VoidFS** (SUSFS 1.4.2 kernel hiding driver & native CLI).

---

## 🔥 Key Features

- **VoidKernel Tightly Coupled**: Native kernel supercall FD integration (`0xDEADBEEF`, `0xCAFEBABE`) for instantaneous, low-overhead root escalation.
- **VoidFS & SUSFS 1.4.2 Control Center**: Machine-readable capability orchestration (`susfs status --json`) with dynamic, capability-driven Jetpack Compose UI cards.
- **Pure Jetpack Compose UI**: Modern, responsive, material design interface with zero-bloat state management.
- **App Profile Management**: Granular per-app root access control and mount namespace isolation.
- **Module Engine**: Module management powered by OverlayFS & Magic Mount.

---

## 🏗️ Architecture & Ecosystem

```text
VoidKernel (Kernel Core & Hooks)
         │
         ├── VoidFS (SUSFS 1.4.2 Engine & Native C CLI)
         │       │
         │       └── /data/adb/ksu/bin/susfs status --json
         │
         ▼
VoidSU Manager (Android Jetpack Compose UI)
```

- **VoidKernel**: Built-in kernel-level root escalation & VFS hooks.
- **VoidFS**: Core hiding driver (`sus_path`, `sus_mount`, `sus_kstat`, `set_uname`, `sus_su`) and native C control CLI.
- **VoidSU**: Presentation & orchestration manager app.

---

## 🔗 Project Links

- **VoidSU Manager**: [github.com/heySaish/VoidSU](https://github.com/heySaish/VoidSU)
- **VoidFS Engine**: [github.com/heySaish/VoidFS](https://github.com/heySaish/VoidFS)
- **Void-Kernel**: [github.com/heySaish/Void-Kernel](https://github.com/heySaish/Void-Kernel)
- **Telegram Channel**: [t.me/VoidKernelOfficial](https://t.me/VoidKernelOfficial)

---

## 📜 License

- **`/kernel` directory**: [GPL-2.0-only](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)
- **All other files**: [GPL-3.0-or-later](https://www.gnu.org/licenses/gpl-3.0.html)

---

## 🙏 Credits & Acknowledgments

- **simonpunk** for SUSFS Engine & Addon
- **tiann** & KernelSU Team for KernelSU foundation
- **Magisk** for Magic Mount concept
