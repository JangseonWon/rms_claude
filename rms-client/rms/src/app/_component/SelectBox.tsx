import style from "@/app/_component/selectBox.module.css"
import React, {useState} from "react";
import {faChevronDown, faChevronUp} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {SelectBoxOption} from "@/model/SelectBoxOption";

type Props = {
    options: SelectBoxOption[]
    label: string
    value?: string
    onChange?: (selectedValue: string) => void;
}

export default function SelectBox({ label, value, options, onChange }: Props) {
    const [selectedLanguage, setSelectedLanguage] = useState<string>('');
    const [isOpen, setIsOpen] = useState<boolean>(false)

    const handleOptionClick = (option: SelectBoxOption) => {
        setSelectedLanguage(option.name!);
        setIsOpen(!isOpen);
        onChange?.(option.value!);
    };
    const toggleList = () => {
        setIsOpen(!isOpen)
    }

    return (
        <div>
            <section className={style.selectSection}>
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