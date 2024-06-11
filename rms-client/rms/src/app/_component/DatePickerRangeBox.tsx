"use client"

import DatePicker, {ReactDatePickerProps} from "react-datepicker";
import {forwardRef, useState} from "react";
import style from "@/app/_component/datePickerRange.module.css"
import "react-datepicker/dist/react-datepicker.css";
import '@/app/globals.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCalendarDays} from "@fortawesome/free-regular-svg-icons";

type Props = {
    label?: string
    value?: Date
    onChange: (from: Date, to: Date) => void
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
                placeholder="DD-MM-YYYY~DD-MM-YYYY"
            />
            <FontAwesomeIcon icon={faCalendarDays} className={style.icon} />
        </div>
    )
);
CustomInput.displayName = "CustomInput";
export default ({label, value, onChange, required = false}: Props) => {
    const [dateRange, setDateRange] = useState<[Date | null, Date | null]>([null, null]);
    const [startDate, endDate] = dateRange;

    return (
        <div className={style.dateBox}>
            <p>{label}</p>
            <DatePicker
                selectsRange={true}
                startDate={startDate}
                endDate={endDate}
                dateFormat={"dd-MM-yyyy"}
                showPopperArrow={false}
                onChange={(update) =>{
                    setDateRange(update);
                    if(update[0] && update[1]) onChange(update[0], update[1])
                }}
                customInput={<CustomInput/>}
            />
        </div>
    )
}
