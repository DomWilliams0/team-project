package com.badlogic.gdx.maps.tiled;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.ImageResolver;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.utils.*;

import java.io.File;
import java.io.IOException;

/**
 * The {@link TmxMapLoader} class but with less GDX.
 * A lot of stolen code...
 * 
 * @author bxd428
 */
public class TmxMapLoaderBasic extends TmxMapLoader {

	@Override
	public FileHandle resolve(String path) {
		return new FileHandle(new File(path));
	}

	/**
	 * Loads the {@link TiledMap} from the given file. The file is resolved via the {@link FileHandleResolver} set in the
	 * constructor of this class. By default it will resolve to an internal file.
	 *
	 * @param fileName   the filename
	 * @param parameters specifies whether to use y-up, generate mip maps etc.
	 * @return the TiledMap
	 */
	@Override
	public TiledMap load(String fileName, TmxMapLoader.Parameters parameters) {
		try {
			this.convertObjectToTileSpace = parameters.convertObjectToTileSpace;
			this.flipY = parameters.flipY;
			FileHandle tmxFile = resolve(fileName);
			root = xml.parse(tmxFile);
			ObjectMap<String, Texture> textures = new ObjectMap<String, Texture>();
			Array<FileHandle> textureFiles = loadTilesets(root, tmxFile);
			textureFiles.addAll(loadImages(root, tmxFile));

			ImageResolver.DirectImageResolver imageResolver = new ImageResolver.DirectImageResolver(textures);
			TiledMap map = loadTilemap(root, tmxFile, imageResolver);
			map.setOwnedResources(textures.values().toArray());
			return map;
		} catch (IOException e) {
			throw new GdxRuntimeException("Couldn't load tilemap '" + fileName + "'", e);
		}
	}

	/**
	 * Loads the specified tileset data, adding it to the collection of the specified map, given the XML element, the tmxFile and
	 * an {@link ImageResolver} used to retrieve the tileset Textures.
	 * <p>
	 * <p>
	 * Default tileset's property keys that are loaded by default are:
	 * </p>
	 * <p>
	 * <ul>
	 * <li><em>firstgid</em>, (int, defaults to 1) the first valid global id used for tile numbering</li>
	 * <li><em>imagesource</em>, (String, defaults to empty string) the tileset source image filename</li>
	 * <li><em>imagewidth</em>, (int, defaults to 0) the tileset source image width</li>
	 * <li><em>imageheight</em>, (int, defaults to 0) the tileset source image height</li>
	 * <li><em>tilewidth</em>, (int, defaults to 0) the tile width</li>
	 * <li><em>tileheight</em>, (int, defaults to 0) the tile height</li>
	 * <li><em>margin</em>, (int, defaults to 0) the tileset margin</li>
	 * <li><em>spacing</em>, (int, defaults to 0) the tileset spacing</li>
	 * </ul>
	 * <p>
	 * <p>
	 * The values are extracted from the specified Tmx file, if a value can't be found then the default is used.
	 * </p>
	 *
	 * @param map           the Map whose tilesets collection will be populated
	 * @param element       the XML element identifying the tileset to load
	 * @param tmxFile       the Filehandle of the tmx file
	 * @param imageResolver the {@link ImageResolver}
	 */
	@Override
	protected void loadTileSet(TiledMap map, XmlReader.Element element, FileHandle tmxFile, ImageResolver imageResolver) {
		if (element.getName().equals("tileset")) {
			String name = element.get("name", null);
			int firstgid = element.getIntAttribute("firstgid", 1);
			int tilewidth = element.getIntAttribute("tilewidth", 0);
			int tileheight = element.getIntAttribute("tileheight", 0);
			int spacing = element.getIntAttribute("spacing", 0);
			int margin = element.getIntAttribute("margin", 0);
			String source = element.getAttribute("source", null);

			int offsetX = 0;
			int offsetY = 0;

			String imageSource = "";
			int imageWidth = 0, imageHeight = 0;

			FileHandle image = null;
			if (source != null) {
				FileHandle tsx = getRelativeFileHandle(tmxFile, source);
				try {
					element = xml.parse(tsx);
					name = element.get("name", null);
					tilewidth = element.getIntAttribute("tilewidth", 0);
					tileheight = element.getIntAttribute("tileheight", 0);
					spacing = element.getIntAttribute("spacing", 0);
					margin = element.getIntAttribute("margin", 0);
					XmlReader.Element offset = element.getChildByName("tileoffset");
					if (offset != null) {
						offsetX = offset.getIntAttribute("x", 0);
						offsetY = offset.getIntAttribute("y", 0);
					}
					XmlReader.Element imageElement = element.getChildByName("image");
					if (imageElement != null) {
						imageSource = imageElement.getAttribute("source");
						imageWidth = imageElement.getIntAttribute("width", 0);
						imageHeight = imageElement.getIntAttribute("height", 0);
						image = getRelativeFileHandle(tsx, imageSource);
					}
				} catch (IOException e) {
					throw new GdxRuntimeException("Error parsing external tileset.");
				}
			} else {
				XmlReader.Element offset = element.getChildByName("tileoffset");
				if (offset != null) {
					offsetX = offset.getIntAttribute("x", 0);
					offsetY = offset.getIntAttribute("y", 0);
				}
				XmlReader.Element imageElement = element.getChildByName("image");
				if (imageElement != null) {
					imageSource = imageElement.getAttribute("source");
					imageWidth = imageElement.getIntAttribute("width", 0);
					imageHeight = imageElement.getIntAttribute("height", 0);
					image = getRelativeFileHandle(tmxFile, imageSource);
				}
			}

			TiledMapTileSet tileset = new TiledMapTileSet();
			tileset.setName(name);
			tileset.getProperties().put("firstgid", firstgid);
			if (image != null) {

				MapProperties props = tileset.getProperties();
				props.put("imagesource", imageSource);
				props.put("imagewidth", imageWidth);
				props.put("imageheight", imageHeight);
				props.put("tilewidth", tilewidth);
				props.put("tileheight", tileheight);
				props.put("margin", margin);
				props.put("spacing", spacing);

				int stopWidth = 628 - tilewidth; // TODO - Should really get the legit numbers not faked.
				int stopHeight = 475 - tileheight;

				int id = firstgid;

				for (int y = margin; y <= stopHeight; y += tileheight + spacing) {
					for (int x = margin; x <= stopWidth; x += tilewidth + spacing) {
						TextureRegion tileRegion = new TextureRegion();
						TiledMapTile tile = new StaticTiledMapTile(tileRegion);
						tile.setId(id);
						tile.setOffsetX(offsetX);
						tile.setOffsetY(flipY ? -offsetY : offsetY);
						tileset.putTile(id++, tile);
					}
				}
			} else {
				Array<XmlReader.Element> tileElements = element.getChildrenByName("tile");
				for (XmlReader.Element tileElement : tileElements) {
					XmlReader.Element imageElement = tileElement.getChildByName("image");
					if (imageElement != null) {
						imageSource = imageElement.getAttribute("source");
						imageWidth = imageElement.getIntAttribute("width", 0);
						imageHeight = imageElement.getIntAttribute("height", 0);
						image = getRelativeFileHandle(tmxFile, imageSource);
					}
					TextureRegion texture = imageResolver.getImage(image.path());
					TiledMapTile tile = new StaticTiledMapTile(texture);
					tile.setId(firstgid + tileElement.getIntAttribute("id"));
					tile.setOffsetX(offsetX);
					tile.setOffsetY(flipY ? -offsetY : offsetY);
					tileset.putTile(tile.getId(), tile);
				}
			}
			Array<XmlReader.Element> tileElements = element.getChildrenByName("tile");

			Array<AnimatedTiledMapTile> animatedTiles = new Array<AnimatedTiledMapTile>();

			for (XmlReader.Element tileElement : tileElements) {
				int localtid = tileElement.getIntAttribute("id", 0);
				TiledMapTile tile = tileset.getTile(firstgid + localtid);
				if (tile != null) {
					XmlReader.Element animationElement = tileElement.getChildByName("animation");
					if (animationElement != null) {

						Array<StaticTiledMapTile> staticTiles = new Array<StaticTiledMapTile>();
						IntArray intervals = new IntArray();
						for (XmlReader.Element frameElement : animationElement.getChildrenByName("frame")) {
							staticTiles.add((StaticTiledMapTile) tileset.getTile(firstgid + frameElement.getIntAttribute("tileid")));
							intervals.add(frameElement.getIntAttribute("duration"));
						}

						AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
						animatedTile.setId(tile.getId());
						animatedTiles.add(animatedTile);
						tile = animatedTile;
					}

					String terrain = tileElement.getAttribute("terrain", null);
					if (terrain != null) {
						tile.getProperties().put("terrain", terrain);
					}
					String probability = tileElement.getAttribute("probability", null);
					if (probability != null) {
						tile.getProperties().put("probability", probability);
					}
					XmlReader.Element properties = tileElement.getChildByName("properties");
					if (properties != null) {
						loadProperties(tile.getProperties(), properties);
					}
				}
			}

			for (AnimatedTiledMapTile tile : animatedTiles) {
				tileset.putTile(tile.getId(), tile);
			}

			XmlReader.Element properties = element.getChildByName("properties");
			if (properties != null) {
				loadProperties(tileset.getProperties(), properties);
			}
			map.getTileSets().addTileSet(tileset);
		}
	}

}
