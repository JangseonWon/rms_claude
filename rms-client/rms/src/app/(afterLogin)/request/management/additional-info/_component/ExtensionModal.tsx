'use client';

import React, {useEffect, useState} from "react";
import style from "./extensionModal.module.css";
import {faMinus, faPlus, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Extension, ExtensionType} from "@/model/Extension";
import {patchExtension} from "@/app/(afterLogin)/request/management/additional-info/_api/patchExtension";
import BlueButton from "@/app/_component/BlueButton";

type Props = {
    extensionData: Extension;
    closeModal: () => void;
    refreshTable: () => void;
}

export default function ExtensionModal({extensionData, closeModal, refreshTable}: Props) {
    const [extension, setExtension] = useState<Extension>(extensionData)
    const [listInputs, setListInputs] = useState<string[]>(['','']);

    const extensionOptions: SelectBoxOption[] = [
        {value: '\\b(?:true|false)\\b', name: ExtensionType.BOOLEAN},
        {value: '.*', name: ExtensionType.STRING},
        {value: '.*', name: ExtensionType.TEXT},
        {value: '-?\\d+', name: ExtensionType.INTEGER},
        {value: '-?\\d+(\\.\\d+)?', name: ExtensionType.FLOAT},
        {name: ExtensionType.LIST}
    ];
    useEffect(() => {
        if (extension.type === ExtensionType.LIST && extension.regex) {
            const splitRegex = extension.regex.replace(/\\b\(\?:|\)\\b/g, '').split('|');
            setListInputs(splitRegex);
        }
    }, [extension]);


    const handleAddInput = () => {
        setListInputs([...listInputs, '']);
    };

    const handleRemoveInput = (index: number) => {
        const newListInputs = listInputs.filter((_, i) => i !== index);
        setListInputs(newListInputs);
    };

    const handleListInputChange = (index: number, value: string) => {
        const newListInputs = [...listInputs];
        newListInputs[index] = value;
        setListInputs(newListInputs);
    };

    const handleUpdateButtonClick = async () => {
        try {
            if(extension.type === ExtensionType.LIST) {
                extension.regex = `\\b(?:${listInputs.filter(input => input).join('|')})\\b`
            }
            await patchExtension(extension);
            alert('Update Complete');
            closeModal();
            refreshTable();
        } catch (error) {
            console.error("Error updating extension:", error);
            alert('An error occurred during update.');
        }
    };

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <section className={style.modalHeader}>
                    <div className={style.modalClose} onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </div>
                    <div className={style.modalTop}>
                        <div className={style.title}>
                            Edit Extension
                        </div>
                    </div>
                </section>
                <section className={style.modalBody}>
                    <div className={style.leftBody}>
                        <div className={style.categoryName}>
                            <InputBox
                                label={"Extension Name"}
                                value={extension.name}
                                disabled={true}
                                required={false}
                            />
                            <SelectBox
                                label={"Extension Type"}
                                value={extension.type}
                                options={extensionOptions}
                                width={'11vw'}
                                required={true}
                                onChange={(selectedOption) => {
                                    setExtension({
                                        ...extension,
                                        type: selectedOption.name,
                                        regex: selectedOption.value
                                    })
                                }}
                            />
                            <BlueButton
                                name={"Save"}
                                onClick={handleUpdateButtonClick}
                            />
                        </div>
                    </div>
                    <div className={style.rightBody}>
                        {extension.type === ExtensionType.LIST && (
                            <>
                            {listInputs.map((input, index) => (
                                    <div key={index} style={{display: 'flex', marginBottom: '2vh'}}>
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
                                <button className={style.addButton} onClick={handleAddInput}>
                                    <FontAwesomeIcon icon={faPlus}/> Add Value
                                </button>
                            </>
                        )}
                    </div>
                </section>
            </div>
        </div>
    )
}