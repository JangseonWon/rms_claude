import style from "@/app/_component/selectBox.module.css"
import React, {useState} from "react";
import {faChevronDown, faChevronUp} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

interface Option {
    name: string;
}
type Props = {
    options: Option[]
    label: string
    onSelectionChange?: (selectedValue: string) => void;
}

export default function SelectBox({ label, options, onSelectionChange }: Props) {
    const [selectedLanguage, setSelectedLanguage] = useState<string>('');
    const [isOpen, setIsOpen] = useState<boolean>(false)

    const handleOptionClick = (language: string) => {
        setSelectedLanguage(language);
        setIsOpen(!isOpen);
        onSelectionChange?.(language);
    };
    const toggleList = () => {
        setIsOpen(!isOpen)
    }


    return (
        <div>
            <section className={style.selectSection}>
                <p className={style.label}>{label}</p>
                <button className={`${style.btnSelect} ${isOpen ? style.open : ''}`} onClick={toggleList}>
                    <div>{selectedLanguage || 'select Service'}</div>
                    <FontAwesomeIcon icon={faChevronDown} />
                </button>
                <ul className={`${style.listMember} ${isOpen ? style.open : ''}`}>
                    {options.map((option) => (
                        <li key={option.name}>
                            <button onClick={() => handleOptionClick(option.name)}>
                                {option.name}
                            </button>
                        </li>
                    ))}
                </ul>
            </section>
        </div>
    )
}