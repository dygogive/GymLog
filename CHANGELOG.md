# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/)
and this project adheres to [Semantic Versioning](https://semver.org/).

---

## [1.1.0] – 2025-05-18

### Fixed
- 🛠 **Critical bug**: Editing a training block during an active workout session caused permanent loss of saved workout results. This has been resolved.
- 🧠 Preserved user data correctly when modifying training block structures mid-session.

### Changed
- 🗄 **Database migration** added to support the fix and ensure data integrity when updating the app from version 1.0.

---

## [1.0.0] – 2025-05-11

### Added
- ✅ **Workout Log**: Log sets, reps, weights, and durations during a training session.
- 🧩 **Training Program Builder**: Construct modular programs using days, blocks, and exercises.
- 🏋️ **Exercise Editor**: Add and manage exercises with filters for muscle group, movement type, and equipment.
- ↕️ **Drag-and-Drop Sorting**: Reorder exercises and blocks easily while editing.
- 📦 Reusable block components across training programs.

### Status
- First public **Minimal Viable Product (MVP)** release.
- Feature-complete for personal gym use, but UI and UX will be improved in upcoming releases.
