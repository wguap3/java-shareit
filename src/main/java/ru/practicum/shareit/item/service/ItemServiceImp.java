package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UnauthorizedActionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper1;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImp(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId);
        Item item = ItemMapper1.toItem(itemDto, ownerId);
        return ItemMapper1.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto editingItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemId);

        if (!existingItem.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedActionException("Редактировать может только владелец");
        }

        ItemMapper1.updateItemFromDto(itemDto, existingItem);

        return ItemMapper1.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        return ItemMapper1.toItemDto(item);
    }


    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findByOwner(ownerId).stream()
                .map(ItemMapper1::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchAvailableByText(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper1::toItemDto)
                .collect(Collectors.toList());
    }

}
