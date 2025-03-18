"use client"

import DatePicker, {ReactDatePickerProps} from "react-datepicker";
import React, {forwardRef, useState} from "react";
import style from "@/app/_component/datePickerRange.module.css"
import "react-datepicker/dist/react-datepicker.css";
import '@/app/globals.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCalendarDays} from "@fortawesome/free-regular-svg-icons";
import {getMonth, getYear} from "date-fns";
import {faAngleLeft, faAngleRight} from "@fortawesome/free-solid-svg-icons";

type Props = {
    label?: string
    value?: Date
    onChange: (from: Date, to: Date) => void
    required?: boolean
    fromDate?: Date | null
    toDate?: Date | null
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
                readOnly={true}
                onClick={onClick}
                onChange={onChange}
                ref={ref}
                placeholder="YYYY-MM-DD~YYYY-MM-DD"
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

export default function DatePickerRangeBox({label, value, onChange, required = false, fromDate, toDate}: Props) {
    const years = range(1900, getYear(new Date()) + 1, 1);
    const [dateRange, setDateRange] = useState<[Date | null, Date | null]>([fromDate || null, toDate || null]);
    const [startDate, endDate] = dateRange;
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
    return (
        <div className={style.dateBox}>
            <p>{label}</p>
            <DatePicker
                renderCustomHeader={({
                         date,
                         changeYear,
                         changeMonth,
                         decreaseMonth,
                         increaseMonth,
                         prevMonthButtonDisabled,
                         nextMonthButtonDisabled,
                     }) => (
                    <div
                        style={{
                            margin: 10,
                            display: "flex",
                            justifyContent: "center",
                        }}
                    >
                        <button onClick={decreaseMonth} className={style.leftButton} disabled={prevMonthButtonDisabled}>
                            <FontAwesomeIcon icon={faAngleLeft}/>
                        </button>
                        <select
                            className={style.selectBox}
                            value={months[getMonth(date)]}
                            onChange={({target: {value}}) =>
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
                            onChange={({target: {value}}) =>
                                changeYear(Number(value))
                            }
                        >
                            {years.map((option) => (
                                <option key={option} value={option}>
                                    {option}
                                </option>
                            ))}
                        </select>
                        <button onClick={increaseMonth} className={style.rightButton}
                                disabled={nextMonthButtonDisabled}>
                            <FontAwesomeIcon icon={faAngleRight}/>
                        </button>
                    </div>
                )}
                selectsRange={true}
                startDate={startDate}
                endDate={endDate}
                dateFormat={"yyyy-MM-dd"}
                showPopperArrow={false}
                onChange={(update) => {
                    const selectedDates = update as unknown as [Date | null, Date | null];

                    setDateRange(selectedDates);

                    if (selectedDates[0] && selectedDates[1]) {
                        onChange(selectedDates[0] as Date, selectedDates[1] as Date);
                    }
                }}
                customInput={<CustomInput/>}
            />
        </div>
    );
}
