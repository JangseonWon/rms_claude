'use client';

import style
    from "@/app/(afterLogin)/request/services/[service]/single/_component/extension/extensionInputComponent.module.css";
import InputBox from "@/app/_component/InputBox";
import React from "react";
import {
    useProband,
    useRelationship,
    useSetProband,
    useSetProbandModalOpen,
    useSetRelationship
} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import SelectBox from "@/app/_component/SelectBox";

export const ProbandComponent = () => {
    const probandValue = useProband();
    const setProbandValue = useSetProband();
    const relationship = useRelationship();
    const setRelationship = useSetRelationship();
    const setProbandModal = useSetProbandModalOpen();

    const relationshipList = [
        {name: "FATHER", value: 'father'},
        {name: "MOTHER", value: 'mother'}
    ];

    const Click = () => {
        setProbandModal(true);
    }

    return (
        <div>
            <p className={style.title}>Proband Info.</p>
            <div key={'proband'} className={style.proband}>
                <SelectBox
                    key={'relationship'}
                    label={'RelationShip*'}
                    value={relationship}
                    options={relationshipList}
                    required={true}
                    onChange={(selectedOption) => setRelationship(selectedOption.name)}
                    width="200px"
                />
                <div className={style.probandInput}>
                    <InputBox
                        key={'probandInput'}
                        label={'Proband Number'}
                        required={true}
                        disabled={true}
                        value={probandValue}
                        onChange={(inputValue) => setProbandValue(inputValue)}
                    />
                </div>
                <button className={style.button} onClick={Click}>Click here to find proband</button>
            </div>
        </div>
    )
}