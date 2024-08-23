'use client';

import React, {useEffect, useState} from "react";
import style from "./extensionModal.module.css";
import {faMinus, faPlus, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Extensions} from "@/model/ServiceExtensionAndSampleType";
import {patchExtension} from "@/app/(afterLogin)/request/management/extension/_api/patchExtension";
import BlueButton from "@/app/_component/BlueButton";

type Props = {
    getExtension?: Extensions;
    type: string;
    open: boolean;
    closeModal: () => void;
    refreshTable: () => void;
}

export default function ExtensionModal({getExtension, type, open, closeModal, refreshTable}: Props) {
    const [extension, setExtension] = useState<Extensions>();
    const [extensionRegex, setExtensionRegex] = useState<string | undefined>();
    const [extensionType, setExtensionType] = useState<string>();
    const [listInputs, setListInputs] = useState<string[]>(['','']);
    const [regexList, setRegexList] = useState<string>('');

    const extensionOptions: SelectBoxOption[] = [
        {value: '\\b(?:true|false)\\b', name: 'Boolean'},
        {value: '.*', name: 'String'},
        {value: '-?\\d+', name: 'Number'},
        {value: regexList, name: 'List'}
    ];

    useEffect(() => {
        setExtension(getExtension);
        setExtensionRegex(type);

        if (type === "List" && getExtension?.regex) {
            const splitRegex = getExtension.regex.replace(/\\b\(\?:|\)\\b/g, '').split('|');
            setListInputs(splitRegex);
        }
    }, [getExtension]);

    useEffect(() => {
        if (extensionRegex === 'List') {
            const regex = `\\b(?:${listInputs.filter(input => input).join('|')})\\b`;
            setRegexList(regex);
            setExtensionType('List');
        }
    }, [listInputs, extensionRegex]);

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
        if (!extension) {
            alert('There is no extended information.');
            return;
        }

        if (!extensionType) {
            alert('Extension Type is not selected.');
            return;
        }

        const updatedRegex = extensionRegex === 'List' ? regexList : extensionType;

        try {
            await patchExtension(extension.id, updatedRegex);
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
                                value={extension?.name}
                                disabled={true}
                                required={false}
                            />
                            <SelectBox
                                label={"Extension Type"}
                                value={extensionRegex}
                                options={extensionOptions}
                                width={'11vw'}
                                required={true}
                                onChange={(value) => {
                                    setExtensionRegex(value.name);
                                    setExtensionType(value.value);
                                }}
                            />
                            <BlueButton
                                name={"Save"}
                                onClick={handleUpdateButtonClick}
                            />
                        </div>
                    </div>
                    <div className={style.rightBody}>
                        {extensionRegex === 'List' && (
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