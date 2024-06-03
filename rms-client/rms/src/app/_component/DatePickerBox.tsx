"use client"

import DatePicker, {ReactDatePickerProps} from "react-datepicker";
import {forwardRef, useEffect, useState} from "react";
import style from "@/app/_component/datePicker.module.css"
import "react-datepicker/dist/react-datepicker.css";
import '@/app/globals.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCalendarDays} from "@fortawesome/free-regular-svg-icons";

type Props = {
    label?: string
    value?: Date
    onChange?: (date: Date) =>void
    required?: boolean;
}
interface CustomInputProps extends Omit<ReactDatePickerProps, 'onChange'> {
    onClick?(): void;
    onChange?(): void;
}
const CustomInput = forwardRef<HTMLInputElement, CustomInputProps>(
    ({ value, onClick, onChange }, ref) => (
        <div className={style.testBox}>
            <input
                type="text"
                value={value}
                onClick={onClick}
                onChange={onChange}
                ref={ref}
                placeholder="DD-MM-YYYY"
            />
            <FontAwesomeIcon icon={faCalendarDays} className={style.icon} />
        </div>
    )
);
CustomInput.displayName = "CustomInput";
export default function DatePickerBox({label, value, onChange, required=false}: Props) {
    const [selectedDate, setSelectedDate] = useState<Date | undefined>(value);
    const [hasError, setHasError] = useState(false);

    const handleDateChange = (date: Date) => {
        setSelectedDate(date);
        if(onChange) onChange(date)
    };
    useEffect(() => {
        setHasError(!selectedDate && required)
    }, [selectedDate, required]);

    return (
        <div className={`${style.dateBox} ${hasError ? style.error : ""}`}>
            <p>{label}</p>
            <DatePicker
                selected={selectedDate}
                dateFormat={"dd-MM-yyyy"}
                showPopperArrow={false}
                onChange={handleDateChange}
                customInput={<CustomInput/>}
            />
        </div>
    )
}
