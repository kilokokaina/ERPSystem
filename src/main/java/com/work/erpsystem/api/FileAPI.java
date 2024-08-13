package com.work.erpsystem.api;

import com.work.erpsystem.exception.NoDBRecord;
import com.work.erpsystem.model.FileModel;
import com.work.erpsystem.model.ItemModel;
import com.work.erpsystem.model.UserModel;
import com.work.erpsystem.repository.FileRepository;
import com.work.erpsystem.service.impl.ItemServiceImpl;
import com.work.erpsystem.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/file")
public class FileAPI {

    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final FileRepository fileRepository;

    private @Value("${upload.path}") String uploadFilePath;

    @Autowired
    public FileAPI(FileRepository fileRepository, ItemServiceImpl itemService, UserServiceImpl userService) {
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    private FileModel saveFile(MultipartFile image, String fileName) {
        try (FileOutputStream writer = new FileOutputStream(String.format(uploadFilePath, fileName))) {
            log.info("Writing " + fileName);
            writer.write(image.getBytes());

        } catch (IOException exception) {
            log.error(exception.getMessage());
            return null;
        }

        FileModel fileModel = new FileModel();
        fileModel.setFilePath(String.format(uploadFilePath, fileName));
        fileModel.setFileName(fileName);

        return fileRepository.save(fileModel);
    }

    @PostMapping("item/{id}")
    public @ResponseBody ResponseEntity<HttpStatus> uploadImage(@PathVariable(value = "id") Long itemId,
                                                                @RequestBody MultipartFile[] images) {
        if (Objects.nonNull(images)) {
            for (MultipartFile image : images) {
                String fileType = Objects.requireNonNull(image.getContentType()).split("/")[0];
                String fileName = UUID.randomUUID() + "." + image.getOriginalFilename();

                if (fileType.equals("image")) {
                    FileModel fileModel = saveFile(image, fileName);

                    try {
                        ItemModel itemModel = itemService.findById(itemId);

                        List<FileModel> imageList = itemModel.getItemImages();
                        imageList.add(fileModel);
                        itemModel.setItemImages(imageList);

                        itemService.update(itemModel);

                    } catch (NoDBRecord exception) {
                        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
                    }
                }
            }
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("user/{id}")
    public @ResponseBody ResponseEntity<HttpStatus> uploadImage(@PathVariable(value = "id") Long userId,
                                                                @RequestBody MultipartFile image) {
        String fileType = Objects.requireNonNull(image.getContentType()).split("/")[0];
        String fileName = UUID.randomUUID() + "." + image.getOriginalFilename();

        if (fileType.equals("image")) {
            FileModel fileModel = saveFile(image, fileName);

            try {
                UserModel userModel = userService.findById(userId);
                userModel.setUserAvatar(fileModel);

                userService.update(userModel);
            } catch (NoDBRecord exception) {
                return ResponseEntity.ok(HttpStatus.NO_CONTENT);
            }
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

}
