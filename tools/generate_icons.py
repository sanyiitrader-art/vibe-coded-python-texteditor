from PIL import Image
import os

icon_sizes = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

script_dir = os.path.dirname(os.path.abspath(__file__))
project_root = os.path.dirname(script_dir)
source_icon = os.path.join(project_root, "icon.png")

if not os.path.exists(source_icon):
    print("icon.png not found in project root. Skipping icon generation.")
    exit(0)

img = Image.open(source_icon)
res_dir = os.path.join(project_root, "app", "src", "main", "res")

for folder, size in icon_sizes.items():
    out_dir = os.path.join(res_dir, folder)
    os.makedirs(out_dir, exist_ok=True)
    
    out_path = os.path.join(out_dir, "ic_launcher.png")
    resized_img = img.resize((size, size), Image.Resampling.LANCZOS)
    resized_img.save(out_path)
    print(f"Generated {out_path}")

    out_path_round = os.path.join(out_dir, "ic_launcher_round.png")
    resized_img.save(out_path_round)
    print(f"Generated {out_path_round}")