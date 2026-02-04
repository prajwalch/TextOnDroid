# Contributing to TextOnDroid

First off, thanks for taking the time to contribute! ❤️

Contributions of all kinds are welcome and truly appreciated.

If you enjoy the project but don’t write code, you can still help a lot by:

- Starring the repository
- Improving documentation (README, CONTRIBUTING, etc.)
- Helping with translations
- Reporting bugs
- Suggesting features or improvements

If you’d like to contribute code, the guidelines below will help you get started.

---

## Development guidelines

### Code style & conventions

- Follow official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use Android Studio’s default formatter (`Ctrl + Alt + L`)
- Prefer clear and descriptive names for variables, functions, classes, and files
- Keep functions, objects and classes small, focused, and readable
- Group related code logically and separate concerns clearly
- Remove unused imports and group related imports together

When in doubt, prefer **readability and simplicity over cleverness**.

---

### Android-specific guidelines

- Follow modern [Android development practices](https://developer.android.com/topic/architecture/recommendations)
- Keep UI logic separate from business logic
- Avoid unnecessary abstractions
- Prefer lifecycle-aware components
- Keep the app lightweight and aligned with its core goals

---

### Commit message style

Please use the Conventional Commits format whenever possible:  
https://www.conventionalcommits.org/

```
<type>: <short description>
```

Common types include:

- `feat` – new feature
- `fix` – bug fix
- `refactor` – code improvement without behavior change
- `chore` – maintenance tasks (Gradle, formatting, tooling)

Examples:

```
feat: Add find and replace support
fix: Handle empty file on save
refactor: Simplify editor state handling
```

---

## Setting up the project

### Prerequisites

- **Android Studio**  
  Latest stable version: https://developer.android.com/studio
- **Git**  
  Required for cloning and managing the repository

---

### Getting started

1. **Fork the repository**  
   Click the **Fork** button at the top-right of the repository page.

2. **Clone your fork**
   ```sh
   git clone git@github.com:<your-username>/TextOnDroid.git
   cd TextOnDroid
   ```

3. **Open the project in Android Studio**
   - Launch Android Studio
   - Select **Open an Existing Project**
   - Navigate to the cloned directory
   - Let Gradle sync complete

4. **Run the app**
   - Connect a physical Android device or start an emulator
   - Click the ▶ Run button in Android Studio

---

## Creating a pull request

1. Create a new branch for your changes
2. Commit your work with a clear commit message
3. Push the branch to your fork
4. Open a pull request via **Contribute → Open pull request**
5. Provide:
   - A clear title
   - A concise description of what changed and why
   - References to related issues (e.g. `Fixes #42`)

Smaller, focused pull requests are easier to review and merge.

---

Thanks again for contributing and helping improve TextOnDroid! 🚀
