'use client';

import { Extension, ExtensionType } from "@/model/Extension";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import TextBox from "@/app/_component/TextBox";
import style from "./extensionInputComponent.module.css";
import React, {useEffect, useState} from "react";
import SearchProbandModal
    from "@/app/(afterLogin)/request/services/[service]/single/_component/extension/SearchProbandModal";
import {Request} from "@/model/Request";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

interface Props {
    request: Request
    schema: Extension[]
    extensions: Extension[]
    onChange: (id: string, value: string) => void
    onProbandSelected: (proband: Request) => void
}

export default function ExtensionInputComponent({request, schema, extensions, onChange, onProbandSelected}: Props) {
    const showAlert = CallAlertDialog();
    const [isProbandOpen, setProbandOpen] = useState(false)
    const [proband, setProband] = useState<Request | undefined>()

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

    return (
        <div className={style.container}>
            <div className={style.gridContainer}>
                {schema.filter(ext =>
                    ext.type === ExtensionType.LIST ||
                    ext.type === ExtensionType.STRING ||
                    ext.type === ExtensionType.INTEGER ||
                    ext.type === ExtensionType.FLOAT ||
                    ext.type === ExtensionType.BOOLEAN)
                    .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                    .map(ext => {
                        const current = extensions.find(e => e.id === ext.id)?.value ?? ""
                        switch (ext.type) {
                            case ExtensionType.LIST:
                                const opts = ext.regex!
                                    .replace(/^\^\(\?:|\)\$$/g, '')
                                    .split("|")
                                    .map(v=> v.trim().replace(/\\([.*+?^${}()|\[\]\\])/g, '$1'))
                                    .map(v => ({name: v, value: v}))
                                return (
                                    <SelectBox
                                        key={ext.id}
                                        label={`${ext.name}${ext.required ? ' *' : ''}`}
                                        options={opts}
                                        value={current}
                                        required={ext.required}
                                        onChange={opt => onChange(ext.id!, opt.value)}
                                    />
                                )
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
                                />;
                            case ExtensionType.STRING:
                            case ExtensionType.INTEGER:
                            case ExtensionType.FLOAT:
                                return (
                                    <InputBox
                                        key={ext.id}
                                        label={`${ext.name}${ext.required ? ' *' : ''}`}
                                        value={current}
                                        required={ext.required}
                                        regex={ext.regex}
                                        onChange={v => onChange(ext.id!, v)}
                                    />
                                )
                            default:
                                return null
                        }
                    })}
            </div>
            {schema.some(ext =>
                    [ExtensionType.PROBAND_SEARCH, ExtensionType.PROBAND_LIST, ExtensionType.REGISTRATION_ID].includes(ext.type!)
                ) && (
                    <>
                        <p className={style.title}>Proband Info.</p>
                        <div className={style.gridContainer}>
                            {schema.filter((ext) => [ExtensionType.PROBAND_SEARCH, ExtensionType.PROBAND_LIST, ExtensionType.REGISTRATION_ID].includes(ext.type!))
                                .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                                .map(ext => {
                                    const current = extensions.find(e => e.id === ext.id)?.value ?? ""
                                    switch (ext.type) {
                                        case ExtensionType.PROBAND_LIST:
                                            const opts = ext.regex!
                                                .replace(/^\^\(\?:|\)\$$/g, '')
                                                .split("|")
                                                .map(v=> v.trim().replace(/\\([.*+?^${}()|\[\]\\])/g, '$1'))
                                                .map(v => ({name: v, value: v}))
                                            return (
                                                <SelectBox
                                                    label={ext.name!}
                                                    value={current}
                                                    options={opts}
                                                    required={ext.required}
                                                    onChange={opt => onChange(ext.id!, opt.value)}
                                                />
                                            )
                                        case ExtensionType.REGISTRATION_ID:
                                            return (
                                                <InputBox
                                                    label={`Registration ID${ext.required ? " *" : ""}`}
                                                    required={ext.required}
                                                    disabled={true}
                                                    value={proband?.sample?.barcode}
                                                />
                                            )
                                        case ExtensionType.PROBAND_SEARCH:
                                            return (
                                                <>
                                                    <InputBox
                                                        disabled={true}
                                                        label={`${ext.name}${ext.required ? " *" : ""}`}
                                                        required={ext.required}
                                                        value={proband?.sample?.patient?.serial}
                                                        onChange={v => onChange(ext.id!, v)}
                                                    />
                                                </>
                                            )
                                        default:
                                            return null
                                    }

                                })}
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
                    </>
                )}
            <div>
                {schema
                    .filter(ext => ext.type === ExtensionType.TEXT)
                    .sort((a, b) => (a.sort_extension ?? 0) - (b.sort_extension ?? 0))
                    .map(ext => {
                        const current = extensions.find(e => e.id === ext.id)?.value ?? ""
                        switch (ext.type) {
                            case  ExtensionType.TEXT:
                                return (
                                    <div key={ext.id}>
                                        <TextBox
                                            disabled={false}
                                            label={`${ext.name}${ext.required ? " *" : ""}`}
                                            value={current}
                                            required={ext.required}
                                            onChange={v => onChange(ext.id!, v)}
                                        />
                                    </div>
                                )
                            default:
                                return null
                        }

                    })}
            </div>
        </div>

    );
}