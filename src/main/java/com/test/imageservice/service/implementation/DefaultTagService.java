package com.test.imageservice.service.implementation;

import com.test.imageservice.models.Tag;
import com.test.imageservice.repository.TagRepository;
import com.test.imageservice.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultTagService implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public Tag getTagByName(String tagName) {
        return tagRepository.findByTagName(tagName);
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }
}