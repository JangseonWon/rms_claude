import React, {ChangeEvent, useEffect, useState} from 'react';
import style from '@/app/_component/textBox.module.css';

type Props = {
    label: string
    value?: any
    onChange?: (value: string) => void
    required?: boolean;
}

export default function TextBox({ label, value, onChange, required=false }: Props) {
    const [inputValue, setInputValue] = useState('');
    const [hasError, setHasError] = useState(false);

    const handleChange = (event: ChangeEvent<HTMLTextAreaElement>) => {
        const newValue = event.target.value;
        setInputValue(newValue);
        if (onChange) {
            onChange(newValue);
        }

        setHasError(required && newValue.trim() === '');
    };

    useEffect(() => {
        setInputValue(value || '');
        setHasError(required && (!value || value.trim() === ''));
    }, [required, value]);

    return (
        <div className={`${style.content} ${hasError ? style.error : ""}`}>
            <p>{label}</p>
            <textarea
                className={style.memo}
                rows={8}
                value={inputValue}
                onChange={handleChange}
            />
        </div>
    );
}