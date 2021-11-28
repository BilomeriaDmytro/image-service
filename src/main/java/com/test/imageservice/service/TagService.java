package com.test.imageservice.service;

import com.test.imageservice.models.Tag;

public interface TagService {

    Tag getTagByName(String tagName);

    Tag save(Tag tag);
}
