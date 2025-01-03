'use client';

import React, {useEffect, useState} from "react";
import style from "./sampleTypeModal.module.css";
import globalStyle from '@/css/modal.module.css';
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import BlueButton from "@/app/_component/BlueButton";
import {SampleType} from "@/model/SampleType";
import {getSampleType} from "@/app/(afterLogin)/request/management/sample-type/_api/getSampleType";
import {patchSampleType} from "@/app/(afterLogin)/request/management/sample-type/_api/patchSampleTypes";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

type Props = {
    sampleTypeId: string;
    closeModal: () => void;
    refreshTable: () => void;
}

export default function SampleTypeModal({sampleTypeId, closeModal, refreshTable}: Props) {
    const [sampleType, setSampleType] = useState<SampleType>()
    const showAlert = CallAlertDialog();

    const fetchSampleType = async (sampleTypeId: string)=>{
        const response = await getSampleType(sampleTypeId)
        const data = await response.json()
        const sampleType = data as SampleType
        setSampleType(sampleType)
        return sampleType
    }


    useEffect(() => {
        fetchSampleType(sampleTypeId)
    }, []);


    const handleUpdateButtonClick = async () => {
        const response = await patchSampleType(sampleType!)
        if(response.ok){
            showAlert("Update successful")
            closeModal()
            refreshTable()
        } else {
            showAlert("Fail update")
        }

    };

    return (
        <div className={globalStyle.modalBackground}>
            <div className={globalStyle.modal}>
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={style.modalCloseButton}/>
                <div className={style.modalTitle}>Edit SampleType</div>
                <div className={style.formGroup}>
                    <InputBox
                        label={"Code"}
                        value={sampleType?.id}
                        disabled={true}
                        required={false}
                    />
                    <InputBox
                        label={"Name(KR)"}
                        value={sampleType?.name_kr}
                        disabled={true}
                        required={false}
                    />
                </div>
                <div className={style.formGroup}>
                    <InputBox
                        label={"Name(EN)"}
                        value={sampleType?.name}
                        disabled={false}
                        required={false}
                        onChange={(value) => {
                            setSampleType((prev) => ({
                                ...prev,
                                name: value
                            }) as SampleType)
                        }}
                    />
                </div>
                <div className={style.buttonGroup}>
                    <BlueButton name={'Save'} onClick={() => handleUpdateButtonClick()}/>
                </div>
            </div>
        </div>
    )
}