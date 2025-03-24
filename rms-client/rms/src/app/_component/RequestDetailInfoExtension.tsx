'use client';

import React, {useEffect} from "react";
import style from './requestDetailInfoExtension.module.css';
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import SearchProbandModal
    from "@/app/(afterLogin)/request/services/[service]/single/_component/extension/SearchProbandModal";
import {
    useProbandModalOpen,
    useProbandRequest,
    useSetProbandModalOpen
} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import {useRequestStore} from "@/store/useRequestStore";

type Props = {
    disabled?: boolean;
}


export default function RequestDetailInfoExtension({disabled=false}: Props) {
    const { request, setRequest } = useRequestStore();
    const probandRequest = useProbandRequest()
    const probandModal = useProbandModalOpen();
    const setProbandModal = useSetProbandModalOpen();

    const Close = () => {setProbandModal(false);}
    const handleRequestChange = (path: string, value: any) => {
        setRequest((prevState) => ({
            ...prevState,
            ...setNestedValue({...prevState}, path, value)
        }));
    };

    const setNestedValue = (object: any, nestedPath: string, newValue: any): any => {
        const [firstKey, ...remainingPathSegments] = nestedPath.split('.');
        if (remainingPathSegments.length === 0) {
            if (Array.isArray(object[firstKey])) {
                const updatedArray = object[firstKey].some((item: any) => item.id === newValue.id)
                    ? object[firstKey].map((item: any) =>
                        item.id === newValue.id ? { ...item, ...newValue } : item
                    )
                    : [...object[firstKey], newValue];
                return { ...object, [firstKey]: updatedArray };
            }
            return { ...object, [firstKey]: newValue };
        }
        return {
            ...object,
            [firstKey]: setNestedValue(object[firstKey] || {}, remainingPathSegments.join('.'), newValue),
        };
    };
    const generateSelectList = (regex: string): { name: string, value: string }[] => {
        if (regex.includes("|")) {
            return regex
                .replace(/\\b|\b/g, '')
                .replace(/\\|\(|\)|\?:/g, '')
                .split('|')
                .filter(value => value.trim() !== '')
                .map(value => ({
                    name: value,
                    value: value
                }));
        }
        return [];
    };
    const probandModalOpen = () => {
        setProbandModal(true);
    }
    useEffect(() => {
        setRequest((prevState) => ({
            ...prevState,
            request_group: {
                id: probandRequest?.request_group?.id
            }
        }));
    }, [probandRequest]);


    const renderExtensionComponent = (extension: Extension) => {
        const selectList = generateSelectList(extension.regex || '');
        switch (extension.type) {
            case ExtensionType.LIST:
                return <SelectBox
                    disabled={disabled}
                    key={extension.id}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    value={extension.value}
                    options={selectList}
                    required={extension.required}
                    onChange={(selectedOption) => handleRequestChange("sample.extensions", { id: extension.id, value: selectedOption.value })}
                    width="200px"
                />;
            case ExtensionType.BOOLEAN:
                const booleanList = [
                    {name: "TRUE", value: true},
                    {name: "FALSE", value: false}
                ];
                return <SelectBox
                    disabled={disabled}
                    key={extension.id}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    value={extension.value}
                    options={booleanList}
                    required={extension.required}
                    onChange={(selectedOption) => handleRequestChange("sample.extensions", { id: extension.id, value: selectedOption.value })}
                    width="200px"
                />;
            case ExtensionType.INTEGER:
            case ExtensionType.STRING:
                return <InputBox
                    disabled={disabled}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    value={extension.value}
                    required={extension.required}
                    onChange={(value) => handleRequestChange("sample.extensions", { id: extension.id, value: value })}
                />;
            case ExtensionType.FLOAT:
                return <InputBox
                    disabled={disabled}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    value={extension.value}
                    required={extension.required}
                    regex={"float"}
                    onChange={(value) => handleRequestChange("sample.extensions", { id: extension.id, value: value })}
                />;
            case ExtensionType.TEXT:
                return <TextBox
                    disabled={disabled}
                    label={`${extension.name}${extension.required ? ' *' : ''}`}
                    value={extension.value}
                    required={extension.required}
                    onChange={(value) => handleRequestChange("sample.extensions", { id: extension.id, value: value })}
                />;
            case ExtensionType.PROBAND_LIST:
                return <SelectBox
                    disabled={disabled}
                    label={extension.name!}
                    value={extension.value}
                    options={selectList}
                    required={true}
                    width="200px"
                    onChange={(selectedOption) => {
                        setRequest((prevState) => ({
                            ...prevState,
                            request_relation: {
                                id: 3
                            }
                        }));
                        handleRequestChange("sample.extensions", {
                            id: extension.id,
                            value: selectedOption.name
                        })
                    }}
                />
            case ExtensionType.PROBAND_SEARCH:
                return <div className={style.probandInput}>
                    <InputBox
                        label={`Registration ID*`}
                        required={true}
                        disabled={true}
                        value={probandRequest?.sample?.barcode}
                    />
                    <InputBox
                        label={`${extension.name}*`}
                        required={true}
                        disabled={true}
                        value={probandRequest?.sample?.patient?.serial}
                        onChange={(value) => {
                            setRequest((prevState) => ({
                                ...prevState,
                                request_relation: {
                                    id: 3
                                }
                            }));
                            handleRequestChange("sample.extensions", {
                                id: extension.id,
                                value: value
                            })
                        }}
                    />
                    { !disabled && (
                        <button className={style.button} onClick={probandModalOpen}>
                            Find Proband
                        </button>
                    )}
                </div>
            default:
                return null;
        }
    };

    return (
        <div className={style.section}>
            <div className={style.gridContainer}>
                {(request?.sample?.extensions ?? [])
                    .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                    .map((extension) =>
                        extension.type === ExtensionType.TEXT ? (
                            <div key={extension.id}>
                                {renderExtensionComponent(extension)}
                            </div>
                        ) : (extension.type === ExtensionType.PROBAND_SEARCH || extension.type === ExtensionType.PROBAND_LIST) ? null : (
                            <div key={extension.id} className={style.gridItem}>
                                {renderExtensionComponent(extension)}
                            </div>
                        )
                    )}
            </div>
            {request?.sample?.extensions?.some((ext) => (ext.type === ExtensionType.PROBAND_SEARCH || ext.type === ExtensionType.PROBAND_LIST)) && (
                <div>
                    <p className={style.title}>Proband Info.</p>
                    <div className={style.proband}>
                        {(request?.sample?.extensions?? [])
                            .filter((ext) => (ext.type === ExtensionType.PROBAND_SEARCH || ext.type === ExtensionType.PROBAND_LIST))
                            .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                            .map((extension) => (
                                <div key={extension.id}>
                                    {renderExtensionComponent(extension)}
                                </div>
                            ))}
                    </div>
                </div>
            )}
            {probandModal && (
                <SearchProbandModal closeModal={Close}/>
            )}
        </div>
    );
}