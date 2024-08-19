"use client"

import DatePicker, {ReactDatePickerProps} from "react-datepicker";
import React, {forwardRef, useEffect, useState} from "react";
import style from "@/app/_component/datePicker.module.css"
import "react-datepicker/dist/react-datepicker.css";
import '@/app/globals.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCalendarDays} from "@fortawesome/free-regular-svg-icons";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";
import {getMonth, getYear} from "date-fns";

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
                readOnly={true}
                onChange={onChange}
                ref={ref}
                placeholder="DD-MM-YYYY"
            />
            <FontAwesomeIcon icon={faCalendarDays} className={style.icon} />
        </div>
    )
);
CustomInput.displayName = "CustomInput";

const range = (start: number, end: number, step: number) => {
    let output = [];
    for (let i = start; i <= end; i += step) {
        output.push(i);
    }
    return output;
};

export default function DatePickerBox({label, value, onChange, required=false}: Props) {
    const years = range(1900, getYear(new Date()) + 1, 1);
    const [selectedDate, setSelectedDate] = useState<Date | undefined>(value);
    const [hasError, setHasError] = useState(false);

    const months = [
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
    ];

    const handleDateChange = (date: Date) => {
        setSelectedDate(date);
        if(onChange) onChange(date);
    };
    useEffect(() => {
        setHasError(!selectedDate && required)
    }, [selectedDate, required]);

    return (
        <div className={`${style.dateBox} ${hasError ? style.error : ""}`}>
            <p>{label}</p>
            <DatePicker
                selected={selectedDate}
                onChange={handleDateChange}
                dateFormat={"dd-MM-yyyy"}
                showPopperArrow={false}
                customInput={<CustomInput />}
                renderCustomHeader={({
                                         date,
                                         changeYear,
                                         changeMonth,
                                         decreaseMonth,
                                         increaseMonth,
                                         prevMonthButtonDisabled,
                                         nextMonthButtonDisabled,
                                     }) => (
                    <div>
                        <button
                            onClick={decreaseMonth}
                            className={style.leftButton}
                            disabled={prevMonthButtonDisabled}
                        >
                            <FontAwesomeIcon icon={faAngleLeft} />
                        </button>
                        <select
                            className={style.selectBox}
                            value={months[getMonth(date)]}
                            onChange={({ target: { value } }) =>
                                changeMonth(months.indexOf(value))
                            }
                        >
                            {months.map((option) => (
                                <option key={option} value={option}>
                                    {option}
                                </option>
                            ))}
                        </select>
                        <select
                            className={style.selectBox}
                            value={getYear(date)}
                            onChange={({ target: { value } }) =>
                                changeYear(Number(value))
                            }
                        >
                            {years.map((option) => (
                                <option key={option} value={option}>
                                    {option}
                                </option>
                            ))}
                        </select>
                        <button
                            onClick={increaseMonth}
                            className={style.rightButton}
                            disabled={nextMonthButtonDisabled}
                        >
                            <FontAwesomeIcon icon={faAngleRight} />
                        </button>
                    </div>
                )}
            />
        </div>
    )
}
