'use client';

import * as React from 'react';
import {useState} from 'react';
import clsx from 'clsx';
import {css, styled} from '@mui/system';
import {Modal as BaseModal} from '@mui/base/Modal';
import style from "@/app/(afterLogin)/user/_component/editModal.module.css";

interface EditModalProps {
    id?: string;
}

export default function EditModal(id?: EditModalProps) {
    const [open, setOpen] = useState(false);
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    return (
        <div>
            <TriggerButton type="button" onClick={handleOpen}>
                Edit
            </TriggerButton>
            <Modal
                open={open}
                onClose={handleClose}
                slots={{ backdrop: StyledBackdrop }}
            >
                <ModalContent>
                    <section className={style.titleSection}>
                        Edit Institution
                    </section>
                    <section className={style.mainSection}>
                        <section>
                            {`left section ${id?.id ?? ''}`}
                        </section>
                        <section>
                            right section
                        </section>
                    </section>
                    <section className={style.buttonSection}>
                        <button>
                            Add
                        </button>
                        <button>
                            Save
                        </button>
                    </section>
                </ModalContent>
            </Modal>
        </div>
    );
}

const Backdrop = React.forwardRef<
    HTMLDivElement,
    { open?: boolean; className: string }
>((props, ref) => {
    const {open, className, ...other} = props;
    return (
        <div
            className={clsx({'base-Backdrop-open': open}, className)}
            ref={ref}
            {...other}
        />
    );
});

const Modal = styled(BaseModal)`
    position: fixed;
    z-index: 1300;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 200px;
`;

const StyledBackdrop = styled(Backdrop)`
    z-index: -1;
    position: fixed;
    inset: 0;
    background-color: rgb(0 0 0 / 0.5);
    -webkit-tap-highlight-color: transparent;
`;

const ModalContent = styled('div')(
    ({theme}) => css`
        font-family: 'IBM Plex Sans', sans-serif;
        font-weight: 500;
        text-align: start;
        position: relative;
    display: flex;
    flex-direction: column;
    gap: 8px;
    overflow: hidden;
    background-color: #fff;
    border-radius: 15px;
    border: 1px solid #DAE2ED;
    box-shadow: 0 4px 12px rgb(0 0 0 / 0.2);
    padding: 24px;
    color: #1C2025;
  `,
);

const TriggerButton = styled('button')(
    ({ theme }) => css`
    border-radius: 15px;
    transition: all 150ms ease;
    cursor: pointer;
    background: linear-gradient(135deg, #182847, #354e6d);
    color: #ffffff;
    margin: 10px;
    box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
    width: 100px;
    padding: 3px 20px;
    border: none;
    font-weight: 600;
    font-size: 13px;

    &:hover {
        background: linear-gradient(135deg, #3c6699, #4aab5c);
        border: none;
    }
  `,
);