import React, {ChangeEvent, useEffect, useState} from 'react';
import style from '@/app/_component/textBox.module.css';

type Props = {
    label: string
    value?: any
    disabled?: boolean
    onChange?: (value: string) => void
    required?: boolean;
    placeholder?: string
    lengthLimit?: number
}

export default function TextBox({label, value, disabled = false, onChange, required=false, lengthLimit, placeholder}: Props) {
    const [inputValue, setInputValue] = useState('');
    const [hasError, setHasError] = useState(false);

    const allowedCharRegex = /[^a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣\s~`!@#$%^&*()\-_=|,.<>/?]/g

    const handleChange = (event: ChangeEvent<HTMLTextAreaElement>) => {
        let newValue = event.target.value
        newValue = newValue.replace(allowedCharRegex, "")

        if (typeof lengthLimit === 'number' && lengthLimit > 0 && newValue.length > lengthLimit) {
            newValue = newValue.slice(0, lengthLimit);
        }

        setInputValue(newValue)
        onChange?.(newValue)

        setHasError(required && newValue.trim() === "")
    }

    useEffect(() => {
        setInputValue(value || '');
        setHasError(required && (!value || value.trim() === ''));
    }, [required, value]);

    return (
        <div className={`${style.content} ${hasError ? style.error : ""}`}>
            <p>{label}</p>
            <textarea
                placeholder={placeholder}
                className={`${style.memo} ${disabled ? style.disable : ""}`}
                rows={8}
                value={inputValue}
                onChange={handleChange}
                disabled={disabled}
            />
        </div>
    );
}