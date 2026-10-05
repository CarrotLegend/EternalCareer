from pathlib import Path

from PIL import Image, ImageDraw


ROOT = Path(__file__).resolve().parents[1]
TEXTURES = ROOT / "src/main/resources/assets/eternal_career/textures"


def create_focus():
    image = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)

    outline = (15, 25, 23, 255)
    metal = (35, 54, 49, 255)
    edge = (71, 103, 86, 255)
    green = (23, 177, 74, 255)
    lime = (126, 246, 68, 255)
    center = (226, 255, 160, 255)

    draw.polygon([(7, 0), (9, 2), (9, 4), (13, 5), (15, 7), (13, 9),
                  (10, 9), (10, 13), (8, 15), (6, 13), (6, 9), (2, 9),
                  (0, 7), (2, 5), (6, 4), (6, 2)], fill=outline)
    draw.polygon([(7, 2), (8, 1), (9, 5), (12, 6), (14, 7), (12, 8),
                  (9, 9), (9, 13), (8, 14), (7, 12), (7, 9), (3, 8),
                  (1, 7), (3, 6), (7, 5)], fill=metal)
    draw.rectangle((2, 6, 4, 7), fill=edge)
    draw.rectangle((11, 6, 13, 7), fill=edge)
    draw.polygon([(8, 2), (11, 7), (8, 13), (5, 7)], fill=green)
    draw.polygon([(8, 3), (10, 7), (8, 12), (6, 7)], fill=lime)
    draw.line([(8, 4), (8, 10)], fill=center)
    draw.point((8, 2), fill=center)
    draw.point((8, 13), fill=green)
    draw.point((3, 7), fill=(120, 217, 103, 255))
    draw.point((12, 7), fill=(120, 217, 103, 255))

    output = TEXTURES / "item/necromancy_laser_focus.png"
    output.parent.mkdir(parents=True, exist_ok=True)
    image.save(output)


def beam_color(position, y, aura):
    segment = position % 35
    taper = min(segment, 34 - segment)
    if aura:
        alpha = 60 + min(7, taper) * 8
        if y % 7 == 0:
            alpha = min(180, alpha + 28)
        return (24, 192 + min(taper * 3, 45), 56, alpha)

    brightness = 255 if y % 4 in (1, 2) else 225
    if taper < 4:
        return (67, 210, 81, 220)
    if y % 5 == 0:
        return (113, 244, 70, 255)
    return (190, brightness, 139, 255)


def create_laser():
    image = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
    pixels = image.load()

    for y in range(0, 37):
        for x in range(0, 72):
            pixels[x, y] = beam_color(x, y, False)

    for y in range(48, 87):
        for x in range(0, 72):
            pixels[x, y] = beam_color(x, y, True)

    output = TEXTURES / "entity/necromancy_laser.png"
    output.parent.mkdir(parents=True, exist_ok=True)
    image.save(output)


if __name__ == "__main__":
    create_focus()
    create_laser()
