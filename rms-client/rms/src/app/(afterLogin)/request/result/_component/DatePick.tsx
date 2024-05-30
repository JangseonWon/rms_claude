'use client';

import DatePickerBox from "@/app/_component/DatePickerBox";

export default function DatePick() {
    return (
        <div style={{display:'flex', gap:'20px'}}>
            <DatePickerBox
                label={"From"}
                onChange={(date) => {
                }}
            />
            <DatePickerBox
                label={"To"}
                onChange={(date) => {
                }}
            />
        </div>
    );
}