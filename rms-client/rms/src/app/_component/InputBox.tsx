"use client"

import style from "@/app/_component/inputBox.module.css"
import {ChangeEventHandler, useEffect, useState} from "react";
type Props = {
    label?: string
    value?: any
    disabled?: boolean
    onChange?: (value: string) => void
    required?: boolean;
}
export default function InputBox({label, value, disabled=false, onChange, required=false}: Props) {
    const [inputValue, setInputValue] = useState('');
    const [hasError, setHasError] = useState(false);

    const onChangeValue: ChangeEventHandler<HTMLInputElement> = (e) => {
        if(onChange) onChange(e.target.value);
        setInputValue(e.target.value);
        setHasError(!e.target.value && required);
    };
    useEffect(() => {
        setInputValue(value || '');
        setHasError(!value && required)
    }, [value]);

    return (
        <div className={`${style.inputBox} ${hasError ? style.error : ""}`}>
            <p className={style.label}>{label}</p>
            <input
                type="text" value={inputValue}
                onChange={onChangeValue}
                disabled={disabled}/>
        </div>
    )
}