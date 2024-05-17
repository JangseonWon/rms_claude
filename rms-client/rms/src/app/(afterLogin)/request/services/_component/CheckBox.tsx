'use client';

import style from "@/app/(afterLogin)/request/services/_component/checkBox.module.css";
import {
    useSetTA0006,
    useSetTA0013,
    useSetTA0014,
    useSetTA0015,
    useSetTA0016,
    useSetTA0017,
    useSetTA0094
} from "@/app/(afterLogin)/request/services/precision-oncology/store/useInputExtensionStore";
import {ChangeEvent} from "react";

interface extensionId {
    value: string
}

export default function CheckBox({value}: extensionId) {
    const setTA0006 = useSetTA0006();
    const setTA0013 = useSetTA0013();
    const setTA0014 = useSetTA0014();
    const setTA0015 = useSetTA0015();
    const setTA0016 = useSetTA0016();
    const setTA0017 = useSetTA0017();
    const setTA0094 = useSetTA0094();

    const handleCheckboxChange = (e: ChangeEvent<HTMLInputElement>) => {
        const isChecked = e.target.checked;

        switch (value) {
            case 'TA0006':
                setTA0006(isChecked);
                break;
            case 'TA0013':
                setTA0013(isChecked);
                break;
            case 'TA0014':
                setTA0014(isChecked);
                break;
            case 'TA0015':
                setTA0015(isChecked);
                break;
            case 'TA0016':
                setTA0016(isChecked);
                break;
            case 'TA0017':
                setTA0017(isChecked);
                break;
            case 'TA0094':
                setTA0094(isChecked);
                break;
            default:
                break;
        }
    };

    return <label form="agree" className={style.checkbox}>
        <input type="checkbox" id={value} onChange={handleCheckboxChange}/>
        <span className={style.on}></span>
    </label>
}