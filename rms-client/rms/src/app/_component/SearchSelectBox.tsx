import style from "@/app/_component/searchSelectBox.module.css"
import React, { useState, useEffect, useRef } from 'react';

export interface Option {
    id: string;
    label: string;
}

interface Props {
    options: Option[];
    placeholder?: string;
    onSelect: (selectedOption: Option | undefined) => void;
    value?: string;
    onChange?: (value:string) => void;
}

export default function SearchSelectBox({ options, placeholder, onSelect, value, onChange }: Props) {
    const [filteredOptions, setFilteredOptions] = useState<Option[]>(options);
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [highlightedIndex, setHighlightedIndex] = useState<number>(-1);
    const inputRef = useRef<HTMLInputElement>(null);
    const listRef = useRef<HTMLUListElement>(null);

    useEffect(() => {
        const searchValue = value?.toLowerCase() ?? '';
        const filtered = options.filter(option =>
            option.label?.toLowerCase().includes(searchValue)
        );
        setFilteredOptions(filtered);
        setHighlightedIndex(-1);
    }, [value, options]);

    useEffect(() => {
        if (highlightedIndex >= 0 && listRef.current) {
            const listItem = listRef.current.children[highlightedIndex] as HTMLElement;
            if (listItem) {
                listItem.scrollIntoView({ block: 'nearest' });
            }
        }
    }, [highlightedIndex]);

    const handleOptionClick = (option: Option) => {
        onSelect(option);
        onChange?.(option.label);
        setIsDropdownOpen(false);
    };
    const handleBlur = () => {
        setTimeout(() => setIsDropdownOpen(false), 100);
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        switch (e.key) {
            case 'ArrowDown':
                e.preventDefault();
                setHighlightedIndex(prev =>
                    prev < filteredOptions.length - 1 ? prev + 1 : 0
                );
                break;
            case 'ArrowUp':
                e.preventDefault();
                setHighlightedIndex(prev =>
                    prev > 0 ? prev - 1 : filteredOptions.length - 1
                );
                break;
            case 'Enter':
                e.preventDefault();
                if (highlightedIndex >= 0) {
                    handleOptionClick(filteredOptions[highlightedIndex]);
                }
                break;
            case 'Escape':
                setIsDropdownOpen(false);
                break;
            default:
                onSelect(undefined)
                setIsDropdownOpen(true)
                break;
        }
    };

    return (
        <div className={style.searchSelectBoxContainer}>
            <input
                ref={inputRef}
                type="text"
                value={value}
                onChange={e => onChange?.(e.target.value)}
                onFocus={() => setIsDropdownOpen(true)}
                onKeyDown={handleKeyDown}
                onBlur={handleBlur}
                placeholder={placeholder || undefined}
                className={style.searchSelectBoxInput}
            />
            {isDropdownOpen && (
                <ul ref={listRef} className={style.searchSelectBoxDropdown}>
                    {filteredOptions.length > 0 ? (
                        filteredOptions.map((option, index) => (
                            <li
                                key={option.id}
                                onClick={() => handleOptionClick(option)}
                                className={`${style.searchSelectBoxOption} ${index === highlightedIndex ? style.highlighted : ''}`}
                                onMouseDown={e => e.preventDefault()}
                            >
                                {option.label}
                            </li>
                        ))
                    ) : (<li className={style.searchSelectBoxNoOptions}>No options found</li>)}
                </ul>
            )}
        </div>
    );
}
