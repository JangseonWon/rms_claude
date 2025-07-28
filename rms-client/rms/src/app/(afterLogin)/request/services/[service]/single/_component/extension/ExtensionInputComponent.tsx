'use client';

import React, {useEffect, useState} from "react";
import {Request} from "@/model/Request"
import style from './extensionInputComponent.module.css';
import {Extension, ExtensionType} from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import SearchProbandModal
    from "@/app/(afterLogin)/request/services/[service]/single/_component/extension/SearchProbandModal";

interface ExtensionInputComponentProps {
    request: Request
    schema: Extension[]
    onChange: (id: string, value: string) => void
    onProbandSelected: (proband: Request) => void
}

export default function ExtensionInputComponent({request, schema, onChange, onProbandSelected}: ExtensionInputComponentProps) {
    const showAlert = CallAlertDialog();
    const [isProbandOpen, setProbandOpen] = useState(false)
    const [proband, setProband] = useState<Request>()


    const generateSelectList = (regex: string): { name: string, value: string }[] => {
        const inner = regex.replace(/^\^\(\?:|\)\$$/g, '');
        return inner
            .split('|')
            .map(v=> v.trim().replace(/\\([.*+?^${}()|\[\]\\])/g, '$1'))
            .filter(item => item !== '')
            .map(item => ({ name: item, value: item }));
    };

    const handleFindProband = () => {
        if (!request.sample?.patient?.organization) {
            showAlert('Please choose the institution');
            return;
        }
        setProbandOpen(true);
    };

    const handleConfirmProband = (proband: Request) => {
        setProband(proband)
        setProbandOpen(false);
        onProbandSelected(proband)
    };

    useEffect(() => {
        if (!proband) return;
        schema.forEach(ext => {
            if (ext.type === ExtensionType.PROBAND_SEARCH) {
                onChange(ext.id!, proband.sample?.patient?.serial ?? "");
            } else if (ext.type === ExtensionType.REGISTRATION_ID) {
                onChange(ext.id!, proband.sample?.barcode ?? "");
            }
        });
    }, [proband]);


    const renderExtensionComponent = (ext: Extension) => {
        const selectList = generateSelectList(ext.regex || '');
        switch (ext.type) {
            case ExtensionType.LIST:
                return <SelectBox
                    key={ext.id}
                    label={`${ext.name}${ext.required ? ' *' : ''}`}
                    options={selectList}
                    required={ext.required}
                    onChange={opt => onChange(ext.id!, opt.value)}
                    width="200px"
                />;
            case ExtensionType.BOOLEAN:
                const booleanList = [
                    {name: "true", value: true},
                    {name: "false", value: false}
                ];
                return <SelectBox
                    key={ext.id}
                    label={`${ext.name}${ext.required ? ' *' : ''}`}
                    options={booleanList}
                    required={ext.required}
                    onChange={opt => onChange(ext.id!, opt.value)}
                    width="200px"
                />;
            case ExtensionType.INTEGER:
            case ExtensionType.STRING:
                return <InputBox
                    key={ext.id}
                    label={`${ext.name}${ext.required ? ' *' : ''}`}
                    required={ext.required}
                    regex = {ext.regex}
                    onChange={v => onChange(ext.id!, v)}
                />;
            case ExtensionType.FLOAT:
                return <InputBox
                    key={ext.id}
                    label={`${ext.name}${ext.required ? ' *' : ''}`}
                    required={ext.required}
                    regex = {ext.regex}
                    onChange={v => onChange(ext.id!, v)}
                />;
            case ExtensionType.TEXT:
                return <TextBox
                    key={ext.id}
                    label={ext.name!}
                    required={ext.required}
                    onChange={v => onChange(ext.id!, v)}
                />;
            case ExtensionType.PROBAND_LIST:
                return <SelectBox
                    key={ext.id}
                    label={ext.name!}
                    options={selectList}
                    required={ext.required}
                    width="200px"
                    onChange={opt => onChange(ext.id!, opt.value)}
                />
            case ExtensionType.REGISTRATION_ID:
                return (
                    <InputBox
                        key={`${ext.name}${ext.id}`}
                        label={`${ext.name}${ext.required ? ' *' : ''}`}
                        required={ext.required}
                        disabled={true}
                        value={proband?.sample?.barcode}
                    />
                )
            case ExtensionType.PROBAND_SEARCH:
                return <div className={style.probandInput}>
                    <InputBox
                        key={`${ext.name}${ext.id}`}
                        label={`${ext.name}${ext.required ? ' *' : ''}`}
                        required={ext.required}
                        disabled={true}
                        value={proband?.sample?.patient?.serial}
                    />
                </div>
            default:
                return null;
        }
    };
    return (
        <>
            <div className={style.gridContainer}>
                {schema
                    .filter(ext =>
                        ext.type === ExtensionType.LIST ||
                        ext.type === ExtensionType.INTEGER ||
                        ext.type === ExtensionType.FLOAT ||
                        ext.type === ExtensionType.STRING ||
                        ext.type === ExtensionType.BOOLEAN
                    )
                    .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                    .map((extension) =>
                        renderExtensionComponent(extension)
                    )
                }
            </div>
            {schema.some((ext) => [ExtensionType.PROBAND_SEARCH, ExtensionType.PROBAND_LIST, ExtensionType.REGISTRATION_ID].includes(ext.type!)) && (
                <div>
                    <p className={style.title}>Proband Info.</p>
                    <div className={style.proband}>
                        {schema.filter((ext) => [ExtensionType.PROBAND_SEARCH, ExtensionType.PROBAND_LIST, ExtensionType.REGISTRATION_ID].includes(ext.type!))
                            .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                            .map((ext) => (
                                <div key={ext.id}>
                                    {renderExtensionComponent(ext)}
                                </div>
                            ))}
                        <button className={style.button} onClick={handleFindProband}>
                            Find Proband
                        </button>
                        {isProbandOpen && (
                            <SearchProbandModal
                                request={request}
                                closeModal={() => setProbandOpen(false)}
                                onConfirm={handleConfirmProband}
                            />
                        )}
                    </div>
                </div>
            )}
            <div>
                {schema.filter(ext => ext.type === ExtensionType.TEXT)
                    .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                    .map((extension) =>
                        renderExtensionComponent(extension)
                    )
                }
            </div>
        </>
    );
}