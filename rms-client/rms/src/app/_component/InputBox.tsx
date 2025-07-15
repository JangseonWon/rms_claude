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
    const [inputValue, setInputValue] = useState<any>(value ?? "")
    const [hasError, setHasError] = useState(false)

    const defaultRegex = /^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9\s~`!@#$%^&*()-_=+\[\]{}\\|;:'",.<>/?]*$/;

    const userRegex = (() => {
        if (!regex) return null
        try {
            return new RegExp(`${regex}`)
        } catch {
            return null
        }
    })()

    const validateTyping = (v: string) => {
        if (v === "") return true
        return defaultRegex.test(v)
    }

    const onChangeValue: ChangeEventHandler<HTMLInputElement> = (e) => {
        const v = e.target.value
        if (!validateTyping(v)) return
        setInputValue(v)
        onChange?.(v)
    }
    const onBlur = () => {
        if (inputValue === "") {
            setHasError(false)
        } else if (userRegex) {
            setHasError(!userRegex.test(inputValue))
        } else {
            setHasError(false)
        }
    }

    useEffect(() => {
        setInputValue(value ?? "")
        setHasError(false)
    }, [value])

    const showRequired = required && inputValue === ""
    const showError    = hasError

    const containerClasses = [
        style.inputBox,
        showRequired ? style.required : "",
        showError    ? style.error    : ""
    ].filter(Boolean).join(" ")

    return (
        <div className={containerClasses}>
            <p>{label}</p>
            <input
                type={type}
                value={inputValue}
                onChange={onChangeValue}
                onBlur={onBlur}
                disabled={disabled}
                placeholder={placeHolder}
            />
        </div>
    )
}