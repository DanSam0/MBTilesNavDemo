package com.dansam0.mbtilesnavdemo.controller;

import org.imintel.mbtiles4j.MBTilesReadException;
import org.imintel.mbtiles4j.MBTilesReader;
import org.imintel.mbtiles4j.Tile;
import org.imintel.mbtiles4j.model.MetadataBounds;
import org.imintel.mbtiles4j.model.MetadataEntry;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tiles")
public class TilesController {

    private final String TILE_DIR = "tilesData/";

    @GetMapping()
    public String getTiles(){

        File folder = new File(TILE_DIR);
        File[] listOfFiles = folder.listFiles();

        String fileNames = "Tilemap list:";

        if(listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    fileNames += "\n" + listOfFiles[i].getName().replaceFirst(".mbtiles", "");
                }
            }
        }

        return fileNames;
    }

    @GetMapping("/search")
    public String getTile(
            @RequestParam(value = "name", required = false) String mapName,
            @RequestParam("z") int zoom,
            @RequestParam("x") int column,
            @RequestParam("y") int row)
            throws MBTilesReadException, IOException {

        if(mapName.isEmpty())
            mapName = "tilesData/one_tile.mbtiles";

        Path path = Paths.get(TILE_DIR + mapName + ".mbtiles");

        if(!Files.exists(path))
            return "Tilemap doesn't exist";

        MBTilesReader r = new MBTilesReader(path.toFile());

        MetadataEntry metadata = r.getMetadata();
        MetadataBounds bounds = metadata.getTilesetBounds();

        Tile targetTile = r.getTile(zoom, column, row);

        String tileData = "no data";

        if (targetTile.getData() != null) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = targetTile.getData().read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            tileData = result.toString(StandardCharsets.UTF_8);
        }

        String tileInfo = "Tilemap name: " + mapName
                + "\nSize: {"
                + "\n   Left: " + bounds.getLeft()
                + "\n   Right: " + bounds.getRight()
                + "\n   Top: " + bounds.getTop()
                + "\n   Bottom: " + bounds.getBottom()
                + "\n}"
                + "\nZoom: " + targetTile.getZoom()
                + "\nColumn: " + targetTile.getColumn()
                + "\nRow: " + targetTile.getRow()
                + "\nTile Data: " + tileData;

        return tileInfo;
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file)
    {
        if (file.isEmpty()) {
            return "File not selected";
        }

        if(!file.getOriginalFilename().contains(".mbtiles"))
            return "File format isn't .mbtiles";

        try {
            Path uploadPath = Paths.get(TILE_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            Files.write(filePath, file.getBytes());

            return "File saved";
        }
        catch (IOException e) {
            e.printStackTrace();
            return "Error while saving file: " + e.getMessage();
        }
    }

}
