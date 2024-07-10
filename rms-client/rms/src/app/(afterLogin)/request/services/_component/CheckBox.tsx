'use client';

import style from "@/app/(afterLogin)/request/services/_component/checkBox.module.css";
import {
    usePushExtensions,
    useSetPushExtensions,
    useSetTA0006,
    useSetTA0013,
    useSetTA0014,
    useSetTA0015,
    useSetTA0016,
    useSetTA0017,
    useSetTA0094
} from "@/app/(afterLogin)/request/services/[service]/single/store/useInputExtensionStore";
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
    const pushExtensions = usePushExtensions();
    const setPushExtensions = useSetPushExtensions();

    const createExtensionArray = (id: string, value: boolean) => {
        const newExtension = {id: id, value};
        const existingIndex = pushExtensions.findIndex(ext => ext.id === id);

        if (existingIndex !== -1) {
            const updatedExtensions = [...pushExtensions];
            updatedExtensions[existingIndex] = newExtension;
            setPushExtensions(updatedExtensions);
        } else {
            setPushExtensions([...pushExtensions, newExtension]);
        }
    }

    const handleCheckboxChange = (e: ChangeEvent<HTMLInputElement>) => {
        const isChecked = e.target.checked;

        switch (value) {
            case 'TA0006':
                setTA0006(isChecked);
                createExtensionArray(value, isChecked);
                break;
            case 'TA0013':
                setTA0013(isChecked);
                createExtensionArray(value, isChecked);
                break;
            case 'TA0014':
                setTA0014(isChecked);
                createExtensionArray(value, isChecked);
                break;
            case 'TA0015':
                setTA0015(isChecked);
                createExtensionArray(value, isChecked);
                break;
            case 'TA0016':
                setTA0016(isChecked);
                createExtensionArray(value, isChecked);
                break;
            case 'TA0017':
                setTA0017(isChecked);
                createExtensionArray(value, isChecked);
                break;
            case 'TA0094':
                setTA0094(isChecked);
                createExtensionArray(value, isChecked);
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