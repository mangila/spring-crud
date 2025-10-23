from pathlib import Path
import subprocess

key_name = "key"
comment = "key pair external access to ec2"

key_file = Path(key_name)

ssh_keygen_command = f"ssh-keygen -t ed25519 -f {key_name} -C \"{comment}\" -N \"\" "

print(ssh_keygen_command)

result = subprocess.run(
    ssh_keygen_command,
    check=True,
    capture_output=True
)

result.check_returncode()

out = result.stdout.decode("utf-8").strip()

print(out)