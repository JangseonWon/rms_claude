import style from "@/app/_component/selectBox.module.css"
import React, {useEffect, useRef, useState} from "react";
import {faChevronDown, faChevronUp} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {SelectBoxOption} from "@/model/SelectBoxOption";

type Props = {
    options: SelectBoxOption[]
    label: string
    value?: any
    onChange?: (selectedValue: any) => void;
    required?: boolean
}

export default function SelectBox({ label, value, options, onChange, required=false }: Props) {
    const [selectedLanguage, setSelectedLanguage] = useState<string>('');
    const [isOpen, setIsOpen] = useState<boolean>(false)
    const [hasError, setHasError] = useState(false);
    const selectBoxRef = useRef<HTMLDivElement>(null);

    const handleOptionClick = (option: SelectBoxOption) => {
        setSelectedLanguage(option.name!);
        setIsOpen(!isOpen);
        onChange?.(option);
    };
    const toggleList = () => {
        setIsOpen(!isOpen)
    }
    const handleClickOutside = (event: MouseEvent) => {
        if (selectBoxRef.current && !selectBoxRef.current.contains(event.target as Node)) {
            setIsOpen(false);
        }
    };
    useEffect(() => {
        setHasError(!selectedLanguage && required);
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [selectedLanguage, value]);

    return (
        <div ref={selectBoxRef}>
            <section className={`${style.selectSection} ${hasError ? style.error : ""}`}>
                <p className={style.label}>{label}</p>
                <button className={`${style.btnSelect} ${isOpen ? style.open : ''}`} onClick={toggleList}>
                    <div>{selectedLanguage || value || '-'}</div>
                    <FontAwesomeIcon icon={faChevronDown} />
                </button>
                <ul className={`${style.listMember} ${isOpen ? style.open : ''}`}>
                    {options.map((option) => (
                        <li key={option.name}>
                            <button onClick={() => handleOptionClick(option)}>
                                {option.name}
                            </button>
                        </li>
                    ))}
                </ul>
            </section>
        </div>
    )
}