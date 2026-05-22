#!/usr/bin/env bash
set -euo pipefail

# Configure rootless Podman storage for better compatibility in Codespaces.
mkdir -p "${HOME}/.config/containers"
cat > "${HOME}/.config/containers/storage.conf" <<'EOF'
[storage]
driver = "overlay"
runroot = "/run/user/1000/containers"
graphroot = "/home/vscode/.local/share/containers/storage"

[storage.options]
mount_program = "/usr/bin/fuse-overlayfs"
EOF

# Install SDKMAN for toolchain management (if not already present).
if [[ ! -s "${HOME}/.sdkman/bin/sdkman-init.sh" ]]; then
  curl -fsSL "https://get.sdkman.io" | bash
fi

# Add SDKMAN init for bash shells.
if ! grep -q "sdkman-init.sh" "${HOME}/.bashrc"; then
  {
    echo ""
    echo "# SDKMAN"
    echo 'export SDKMAN_DIR="${HOME}/.sdkman"'
    echo '[[ -s "${SDKMAN_DIR}/bin/sdkman-init.sh" ]] && source "${SDKMAN_DIR}/bin/sdkman-init.sh"'
  } >> "${HOME}/.bashrc"
fi

# Install Gradle via SDKMAN when available (Java is installed via devcontainer feature).
source "${HOME}/.sdkman/bin/sdkman-init.sh"
sdk install gradle || true

echo "Setup complete."
echo "- Java is available via devcontainer Java feature."
echo "- Gradle is available via SDKMAN (and feature fallback)."
echo "- Podman is installed. Use: podman info"
