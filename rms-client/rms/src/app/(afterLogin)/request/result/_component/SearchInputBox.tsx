'use client';

import InputBox from "@/app/_component/InputBox";
import style from "@/app/(afterLogin)/request/result/_component/searchInputBox.module.css";

export default function SearchInputBox() {
    return (
        <div className={style.container}>
            <select className={style.selectBox}>
                <option key="status" value="status">Status</option>
                <option key="mrn" value="mrn">MRN</option>
                <option key="patient" value="patient">Patient</option>
                <option key="registrationNumber" value="registrationNumber">Registration Number</option>
                <option key="physician" value="physician">Physician</option>
            </select>
            <div className={style.searchInput}>
            <InputBox
                    label={"Search"}
                />
            </div>
        </div>
    );
}