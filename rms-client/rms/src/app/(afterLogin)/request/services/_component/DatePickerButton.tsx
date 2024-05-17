'use client'

import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";
import {DatePicker} from "@mui/x-date-pickers";
import {LocalizationProvider} from "@mui/x-date-pickers/LocalizationProvider";
import {
    useSelectBirth,
    useSelectCollection
} from "@/app/(afterLogin)/request/services/precision-oncology/store/useDatePickerStore";
import dayjs from "dayjs";

interface SelectDate {
    value: string;
}

export default function DatePickerButton( { value }: SelectDate)  {
    const setCollection = useSelectCollection();
    const setBirth = useSelectBirth();

    const handelChange = (date: any) => {
        const selectedYear = dayjs(date).year();
        const selectedMonth = dayjs(date).month() + 1;
        const selectedDate = dayjs(date).date();
        const FullDate = dayjs(date).format("DD/MM/YYYY");

        switch (value) {
            case "collection":
                setCollection({ year: selectedYear, month: selectedMonth, day: selectedDate, fullDate: FullDate });
                console.log(selectedMonth);
                break;
            case "birth":
                setBirth({ year: selectedYear, month: selectedMonth, day: selectedDate, fullDate: FullDate });
                break;
            default:
                break;
        }
    }

    return <LocalizationProvider dateAdapter={AdapterDayjs}>
        <DatePicker
            showDaysOutsideCurrentMonth
            slotProps={{ calendarHeader: { format: 'MM/YYYY' } }}
            onChange={handelChange}
            format={'DD/MM/YYYY'}
            sx={{
                "& .MuiInputBase-root.MuiOutlinedInput-root .MuiOutlinedInput-notchedOutline": {
                    borderColor: '#DAE2ED',
                    color: '#1C2025',
                    boxShadow: '0px 2px 2px #F3F6F9',
                    lineHeight: 1.5,
                    borderRadius: 5,
                },
                ".MuiInputBase-root.MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline": {
                    borderColor: "#90BA2D",
                },
                ".MuiInputBase-input.MuiOutlinedInput-input": {
                    padding: 1,
                    paddingLeft: 2,
                    width: 120,
                },
                ".MuiSvgIcon-root": {
                    color: "#c3c3cb",
                },
                "&:hover": {
                    "& .MuiInputBase-root.MuiOutlinedInput-root .MuiOutlinedInput-notchedOutline": {
                        borderColor: "#90BA2D",
                        borderWidth: 1
                    },
                },
            }}
        />
    </LocalizationProvider>
}