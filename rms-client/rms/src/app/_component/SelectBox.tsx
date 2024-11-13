import style from "@/app/_component/selectBox.module.css"
import React, {useEffect, useRef, useState} from "react";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {SelectBoxOption} from "@/model/SelectBoxOption";

type Props = {
    options: SelectBoxOption[]
    label: string
    value?: any
    onChange?: (selectedValue: SelectBoxOption) => void;
    required?: boolean
    width?: string;
}

export default function SelectBox({ label, value, options, onChange, required=false, width }: Props) {
    const [selectedValue, setSelectedValue] = useState<string>('');
    const [isOpen, setIsOpen] = useState<boolean>(false)
    const [hasError, setHasError] = useState<boolean | undefined>(false);
    const selectBoxRef = useRef<HTMLDivElement | null>(null);

    const handleOptionClick = (option: SelectBoxOption) => {
        setSelectedValue(option.name!);
        setIsOpen(!isOpen);
        onChange?.(option);
    };
    const toggleList = () => {
        setIsOpen(!isOpen)
    }
    const handleClickOutside = (event: MouseEvent) => {
        if (selectBoxRef.current && !selectBoxRef.current!.contains(event.target as Node)) {
            setIsOpen(false);
        }
    };
    useEffect(() => {
        setHasError(!selectedValue && required && !value);
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [selectedValue, required, value]);

    return (
        <div ref={selectBoxRef} className={style.selectContainer} style={{ width }}>
            <section className={`${style.selectSection} ${hasError ? style.error : null}`}>
                <p className={style.label}>{label}</p>
                <button className={`${style.btnSelect} ${isOpen ? style.open : ''}`} onClick={toggleList}>
                    <div>{value || '-'}</div>
                    <FontAwesomeIcon style={{paddingLeft: '1vw'}} icon={faChevronDown} />
                </button>
                <ul className={`${style.listMember} ${isOpen ? style.open : ''}`} style={{ width }}>
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