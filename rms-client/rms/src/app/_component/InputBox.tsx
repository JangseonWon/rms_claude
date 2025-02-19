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
    const [inputValue, setInputValue] = useState(value || '');
    const [hasError, setHasError] = useState(!value && required);

    const defaultRegex = /^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9\s~`!@#$%^&*()-_=+\[\]{}\\|;:'",.<>/?]*$/;
    const floatRegex = /^-?\d*(\.\d*)?$/;

    const getSafeRegex = () => {
        if (regex === "float") return floatRegex;
        if (!regex) return defaultRegex;
        try {
            return new RegExp(`^${regex}$`);
        } catch {
            return defaultRegex;
        }
    };

    const validateInput = (value: string) => {
        if (value === "") return true;
        const pattern = getSafeRegex();
        return pattern.test(value);
    };

    const onChangeValue: ChangeEventHandler<HTMLInputElement> = (e) => {
        const { value } = e.target;

        if (validateInput(value)) {
            setInputValue(value);
            if (onChange) onChange(value);
            setHasError(!value && required);
        } else if (value === "") {
            setInputValue("");
            if (onChange) onChange("");
            setHasError(required);
        }
    };

    useEffect(() => {
        if (value !== undefined && value !== null && value !== inputValue) {
            setInputValue(value);
            setHasError(!value && required);
            if (onChange) onChange(value); // onChange 호출 추가
        }
    }, [value, required]);

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