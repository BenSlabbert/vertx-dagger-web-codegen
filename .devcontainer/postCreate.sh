#!/usr/bin/env bash
set -euo pipefail

# Configure rootless Podman storage for better compatibility in Codespaces.
PODMAN_UID="$(id -u)"
PODMAN_RUNROOT="/run/user/${PODMAN_UID}/containers"
PODMAN_GRAPHROOT="${HOME}/.local/share/containers/storage"

mkdir -p "${HOME}/.config/containers"
cat > "${HOME}/.config/containers/storage.conf" <<EOF
[storage]
driver = "overlay"
runroot = "${PODMAN_RUNROOT}"
graphroot = "${PODMAN_GRAPHROOT}"

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

# Install SDKMAN-managed tools from pinned versions in .sdkmanrc.
source "${HOME}/.sdkman/bin/sdkman-init.sh"
if [[ -f ".sdkmanrc" ]]; then
  sdk env install
  gradle_status="- Gradle is available via SDKMAN using the pinned version from .sdkmanrc."
else
  gradle_status="- Gradle was not installed via SDKMAN because .sdkmanrc was not found."
fi

echo "Setup complete."
echo "- Java is available via devcontainer Java feature."
echo "${gradle_status}"
echo "- Podman is installed. Use: podman info"
