"use client"

import style from "@/app/_component/inputBox.module.css"
import {ChangeEventHandler, useEffect, useState} from "react";

type Props = {
    label?: string
    value?: any
    disabled?: boolean
    onChange?: (value: string) => void
    required?: boolean;
    type?: string;
}
export default function InputBox({label, value, disabled=false, onChange, required=false, type="text"}: Props) {
    const [inputValue, setInputValue] = useState('');
    const [hasError, setHasError] = useState(false);

    const onChangeValue: ChangeEventHandler<HTMLInputElement> = (e) => {
        const { value } = e.target;
        const regex = /^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣\s]*$/;

        if (regex.test(value)) {
            if (onChange) onChange(value);
            setInputValue(value);
            setHasError(!value && required);
        }
    };

    useEffect(() => {
        setInputValue(value || '');
        setHasError(!value && required)
    }, [required, value]);

    return (
        <div className={`${style.inputBox} ${hasError ? style.error : ""}`}>
            <p className={style.label}>{label}</p>
            <input
                type={type}
                value={inputValue}
                onChange={onChangeValue}
                disabled={disabled}/>
        </div>
    )
}