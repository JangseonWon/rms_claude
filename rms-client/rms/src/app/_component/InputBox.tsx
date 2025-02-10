"use client"

import style from "@/app/_component/inputBox.module.css"
import {ChangeEventHandler, useEffect, useState} from "react";

type Props = {
    label?: string
    value?: any
    regex?: string
    disabled?: boolean
    onChange?: (value: string) => void
    required?: boolean;
    type?: string;
    placeHolder?: string;
}
export default function InputBox({label, value, regex, disabled=false, onChange, required=false, type="text", placeHolder}: Props) {
    const [inputValue, setInputValue] = useState('');
    const [hasError, setHasError] = useState(false);

    const defaultRegex = /^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9\s~`!@#$%^&*()-_=+[\]{}\\|;:'",.<>/?]*$/;

    const getSafeRegex = () => {
        if (!regex) return defaultRegex;
        try {
            return new RegExp(`^${regex}$`);
        } catch {
            return defaultRegex;
        }
    };

    const validateInput = (value: string) => {
        const pattern = getSafeRegex();
        return pattern.test(value);
    };

    const onChangeValue: ChangeEventHandler<HTMLInputElement> = (e) => {
        const { value } = e.target;

        if (validateInput(value)) {
            if (onChange) onChange(value);
            setInputValue(value);
            setHasError(!value && required);
        }
    };

    useEffect(() => {
        setInputValue(value !== undefined && value !== null ? value : '');
        setHasError(!value && required)
    }, [required, value]);

    return (
        <div className={`${style.inputBox} ${hasError ? style.error : ""}`}>
            <p>{label}</p>
            <input
                type={type}
                value={inputValue}
                onChange={onChangeValue}
                disabled={disabled}
                placeholder={placeHolder}
            />
        </div>
    )
}