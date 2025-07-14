'use client';

import React, {useEffect, useState} from "react";
import style from "./extensionModal.module.css";
import scrollbar from '@/css/scrollBar.module.css';
import globalStyle from '@/css/modal.module.css';
import {faMinus, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Extension, ExtensionType} from "@/model/Extension";
import BlueButton from "@/app/_component/BlueButton";
import GreenButton from "@/app/_component/GreenButton";
import {getExtension} from "@/app/(afterLogin)/request/management/additional-info/_api/getExtension";
import {patchExtension} from "@/app/(afterLogin)/request/management/additional-info/_api/patchExtension";
import classNames from "classnames";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

type Props = {
    extensionId: string;
    closeModal: () => void;
    refreshTable: () => void;
}
const escapeForRegex = (str: string): string => {
    return str.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
};
const extensionOptions: SelectBoxOption[] = [
    {value: '^(?:true|false)$', name: ExtensionType.BOOLEAN},
    {value: '.*', name: ExtensionType.STRING},
    {value: '.*', name: ExtensionType.TEXT},
    {value: '^-?\\d+$', name: ExtensionType.INTEGER},
    {value: '^-?\\d+(\\.\\d+)?$', name: ExtensionType.FLOAT},
    {name: ExtensionType.LIST},
    {name: ExtensionType.PROBAND_SEARCH},
    {name: ExtensionType.PROBAND_LIST},
];
export default function ExtensionModal({extensionId, closeModal, refreshTable}: Props) {
    const [extension, setExtension] = useState<Extension>()
    const [listInputs, setListInputs] = useState<string[]>(['','']);
    const showAlert = CallAlertDialog();

    const fetchExtension = async (extensionId: string)=>{
        const response = await getExtension(extensionId)
        const data = await response.json()
        const ext = data as Extension
        setExtension(ext)
        if ((ext.type === ExtensionType.LIST || ext.type === ExtensionType.PROBAND_LIST) && ext.regex) {
            const inner = ext.regex.replace(/^\^\(\?:|\)\$$/g, '');
            const values = inner.split('|').map(item => item.replace(/\\(.)/g, '$1'));
            setListInputs(values);
        }
        return ext
    }


    useEffect(() => {
        fetchExtension(extensionId)
    }, []);


    const handleAddInput = () => {
        setListInputs([...listInputs, '']);
    };
    const updateRegex = (inputs: string[]) => {
        const escaped = inputs.filter(v => v).map(escapeForRegex).join('|');
        setExtension(prev => prev && ({
            ...prev,
            regex: `^(?:${escaped})$`,
        }));
    };

    const handleRemoveInput = (index: number) => {
        const newList = listInputs.filter((_, i) => i !== index);
        setListInputs(newList);
        updateRegex(newList);
    };

    const handleListInputChange = (index: number, value: string) => {
        const newList = [...listInputs];
        newList[index] = value;
        setListInputs(newList);
        updateRegex(newList);
    };


    const handleUpdateButtonClick = async () => {
        if (!extension) return;
        const response = await patchExtension(extension);
        if (response.ok) {
            showAlert("Update successful");
            closeModal();
            refreshTable();
        } else {
            showAlert("Fail update");
        }
    };

    return (
        <div className={globalStyle.modalBackground}>
            <div className={globalStyle.modal}>
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={style.modalCloseButton}/>
                <div className={style.modalTitle}>Edit Extension</div>
                <div>
                    <InputBox
                        label={"Code"}
                        value={extension?.id}
                        disabled={true}
                        required={false}
                    />
                    <InputBox
                        label={"Name(KR)"}
                        value={extension?.name_kr}
                        disabled={true}
                        required={false}
                    />
                </div>
                <div>
                    <SelectBox
                        label={"Extension Type"}
                        value={extension?.type}
                        options={extensionOptions}
                        required={true}
                        onChange={(selectedOption) => {
                            setExtension({
                                ...extension,
                                type: selectedOption.name,
                                regex: selectedOption.value
                            })
                        }}
                    />
                    <InputBox
                        label={"Name(EN)"}
                        value={extension?.name}
                        disabled={false}
                        required={false}
                        onChange={(value) => {
                            setExtension((prev) => ({
                                ...prev,
                                name: value
                            }))
                        }}
                    />
                </div>
                {(extension?.type === ExtensionType.LIST || extension?.type === ExtensionType.PROBAND_LIST) && (
                        <div className={scrollbar.wrapper}>
                            <div className={classNames(style.content, scrollbar.default)}>
                                <div className={style.contentTitle}>
                                    <div>Extension Values</div>
                                </div>
                                {listInputs.map((input, index) => (
                                    <div key={index} className={style.formGroup}>
                                        <InputBox
                                            key={index}
                                            label={`List Value ${index + 1}`}
                                            value={input}
                                            required={true}
                                            onChange={(value) => handleListInputChange(index, value)}
                                        />
                                        {listInputs.length > 2 && (
                                            <FontAwesomeIcon
                                                className={style.minusButton}
                                                icon={faMinus}
                                                onClick={() => handleRemoveInput(index)}
                                            />
                                        )}
                                    </div>
                                ))}
                                <div className={style.addButton}>
                                    <GreenButton name={"+Add Value"} onClick={handleAddInput}/>
                                </div>
                            </div>
                        </div>
                    )}
                <div className={style.buttonGroup}>
                    <BlueButton name={'Save'} onClick={() => handleUpdateButtonClick()}/>
                </div>
            </div>
        </div>
    )
}