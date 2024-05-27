"use client"

import DatePicker, {ReactDatePickerProps} from "react-datepicker";
import {forwardRef, useState} from "react";
import style from "@/app/_component/datePicker.module.css"
import "react-datepicker/dist/react-datepicker.css";
import '@/app/globals.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCalendarDays} from "@fortawesome/free-regular-svg-icons";

type Props = {
    label?: string
    value?: Date
    onChange?: (date: Date) =>void
}
interface CustomInputProps extends Omit<ReactDatePickerProps, 'onChange'> {
    onClick?(): void;
    onChange?(): void;
}
const CustomInput = forwardRef<HTMLInputElement, CustomInputProps>(
    ({ value, onClick, onChange }, ref) => (
        <div className={style.testBox}>
            <input type="text" value={value} className={style.customInput} onClick={onClick} onChange={onChange} ref={ref}/>
            <FontAwesomeIcon icon={faCalendarDays} className={style.icon} />
        </div>
    )
);
export default function DatePickerBox({label, value, onChange}: Props) {
    const [selectedDate, setSelectedDate] = useState<Date | undefined>(value);
    const handleDateChange = (date: Date) => {
        setSelectedDate(date);
        if(onChange) onChange(date)
    };
    return (
        <div className={style.dateBox}>
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
