#!/bin/bash 

# based on https://tfswitch.warrensbox.com/Continuous-Integration/

echo "Installing tfswitch locally"

# Get the installer on to your machine
curl https://raw.githubusercontent.com/warrensbox/terraform-switcher/release/install.sh --output installer.sh

# Make installer executable
chmod 755 installer.sh

# Install tfswitch in a location you have permission
./installer.sh -b $(pwd)/.bin

# set custom bin path
CUSTOMBIN=$(pwd)/.bin

#Add custom bin path to PATH environment
export PATH=$CUSTOMBIN:$PATH

$CUSTOMBIN/tfswitch -b $CUSTOMBIN/terraform

terraform $*