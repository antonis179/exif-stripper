## Docker

Below commands were tested with ubuntu 18.04. Use at your discretion.

- `apt install apt-transport-https ca-certificates curl software-properties-common`
- `curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -`
- `add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"`
- `apt install docker-ce docker-compose`
- For execution without sudo: `sudo usermod -aG docker ${USER} `
  - Apply the new membership: `su - ${USER}`



## Portainer

- https://portainer.io/install.html
- Download compose file
- `docker-compose up`



## Mainframer

Change `gradle.properties` to your liking. This will be used as the properties file for all gradle builds. If you do not want this behavior remove the copy command from the Dockerfile.

- Copy android_mainframer to remote machine
- In the copied folder run `docker build -t <image-name> . `
  - Note the "." at the end. It refers to the current folder
- Run `docker run --name “name-of-container" -d <image-name> `
  - you can verify by running: `docker ps`
- Through Portainer (or your UI / CLI of choice) expose an ssh port for the container you ran (e.g. 22 of container maps to 2222 of host)

**Note:** user/pass are root/root by default



## SSH

Below I will lay out the details for MacOS but you can translate it to whatever host you are using.

- Create keys: `ssh-keygen -t rsa`

- Copy key to docker container: `ssh-copy-id -i <public_key_path> root@<ip_address> -p <exported_port>`

  - this tool can be installed by: `brew install ssh-copy-id`

- Add a new host to ~/.ssh/config

  ```properties
  Host {REMOTE_MACHINE_ALIAS}
    User {REMOTE_MACHINE_USERNAME}
    HostName {REMOTE_MACHINE_IP_OR_HOSTNAME}
    Port 22
    IdentityFile ~/.ssh/{SSH_KEY_NAME}
    PreferredAuthentications publickey
    ControlMaster auto
    ControlPath /tmp/%r@%h:%p
    ControlPersist 1h
  ```

- Add the public key to the keychain: `ssh-add -K <public_key>`



## Android Studio

- Plugin: https://github.com/elpassion/mainframer-intellij-plugin

- In .mainframer/config add:

  ```properties
  # Mandatory
  remote_machine={REMOTE_MACHINE_ALIAS}
  
  # Optional - possible values 0-9
  local_compression_level={LEVEL}
  remote_compression_level={LEVEL}
  ```

- Reference: https://github.com/gojuno/mainframer/blob/development/docs/CONFIGURATION.md

- Run any gradle command through mainframer like this

  ```bash
  ./mainframer ./gradlew assembleDebug
  ```


### IntelliJ Run Configuration Setup

1. `Run` → `Edit Configuration` → `+`.
* Select configuration type your typical run task is.
  * Name: something meaningful, like `*-remote-build`.
* Remove standard `Make`/etc step from `Before Launch` section (ha!).
* Create a step in `Before Launch` section for `Run External Tool`.
  * Name: use something meaningful, like `remote assembleDebug`.
  * Program: `bash`.
  * Parameters: `mainframer somebuildcommand with parameters` or any command you want\*.
  * Working directory: `$ProjectFileDir$`.

This also works for tests, you can compile tests on remote machine and then IntelliJ will simply run compiled code on your local machine.

Tip: you can override default run configuration options to to run tasks through mainframer by default.

Reference: https://github.com/gojuno/mainframer/blob/development/recipes/INTELLIJ_RUN_CONFIG.md