package br.edu.ifms.view;

import br.edu.ifms.dal.ConectaBd;
import br.edu.ifms.model.bean.Musica;
import br.edu.ifms.model.bean.Artista;
import br.edu.ifms.model.bean.Compositor;
import br.edu.ifms.model.bean.Album;
import br.edu.ifms.model.dao.AlbumDAO;
import br.edu.ifms.model.dao.ArtistaDAO;
import br.edu.ifms.model.dao.CompositorDAO;
import br.edu.ifms.model.dao.MusicaDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.proteanit.sql.DbUtils;

public class formCadMusicas extends javax.swing.JInternalFrame {

    Connection conecta = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    String filename = "";

    public formCadMusicas() {
        initComponents();
        txtCodigo.setText("");
        this.setLocation(350, 100);
        conecta = ConectaBd.getConnection();
        listarMusica();
        populaComboBoxArtista();
        populaComboBoxCompositor();
        populaComboBoxAlbum();
    }

    /*
    public void fileSaver() throws IOException {
        // Get paths for input and target files.
        FileSystem system = FileSystems.getDefault();
        Path original = system.getPath(musicpath.getText());
        //Path target = system.getPath("/br/edu/ifms/music/");
        Path target = system.getPath("D:\\JAVA\\Eu Escolho Deus - Thalles Roberto.mp3");
        
        //Copia do caminho original para o projeto
        Files.copy(original, target);

        System.out.println("Musica salva!");
    }
     */
    public void fileSaver() throws FileNotFoundException, IOException {
        FileChannel origemChannel = new FileInputStream(musicpath.getText()).getChannel();

        String mus = musicpath.getText();
        String[] musica = filename.split(".wav");
        FileChannel destinoChannel = new FileOutputStream("./src/br/edu/ifms/music/" + musica[0] + ".wav").getChannel();

        destinoChannel.transferFrom(origemChannel, 0, origemChannel.size());
        origemChannel.close();
        destinoChannel.close();

    }

    public void populaComboBoxArtista() {
        ComboBoxArtista.removeAllItems();

        ArtistaDAO aDao = new ArtistaDAO();

        ComboBoxArtista.addItem("Selecione...");
        for (Artista a : aDao.read()) {
            ComboBoxArtista.addItem(a);

        }
    }

    public void populaComboBoxCompositor() {
        ComboBoxCompositor.removeAllItems();

        CompositorDAO cDao = new CompositorDAO();
        ComboBoxCompositor.addItem("Selecione...");
        for (Compositor c : cDao.read()) {
            ComboBoxCompositor.addItem(c);
        }
    }

    public void populaComboBoxAlbum() {
        ComboBoxAlbum.removeAllItems();

        AlbumDAO albDao = new AlbumDAO();
        ComboBoxAlbum.addItem("Selecione...");
        for (Album alb : albDao.read()) {
            ComboBoxAlbum.addItem(alb);
        }
    }

    public void listarMusica() {
        String sql = "SELECT idmusica AS Código, nome_musica AS Música, artista.nome_artista AS Artista, "
                + "compositor.nome_compositor AS Compositor, "
                + "album.album AS Álbum FROM musica m INNER JOIN artista ON(m.idartista = artista.idartista) "
                + "INNER JOIN compositor ON(m.idcompositor = compositor.idcompositor) "
                + "INNER JOIN album ON(m.idalbum = album.idalbum)";

        try {
            pst = conecta.prepareStatement(sql);
            rs = pst.executeQuery();
            tblMusicas.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }

    public void pesquisarMusicas() {
        String sql = "Select m.idmusica Código, m.nome_musica Música, artista.nome_artista Artista, "
                + "compositor.nome_compositor Compositor, album.album Álbum FROM musica m "
                + "INNER JOIN artista ON(m.idartista = artista.idartista)"
                + "INNER JOIN compositor ON(m.idcompositor = compositor.idcompositor)"
                + "INNER JOIN album ON(m.idalbum = album.idalbum)"
                + "WHERE m.nome_musica like ? ORDER BY m.nome_musica";

        try {
            pst = conecta.prepareStatement(sql);
            pst.setString(1, txtPesquisar.getText() + "%");
            rs = pst.executeQuery();
            tblMusicas.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }

    public void cadastrarMusica() throws IOException {

        Musica m = new Musica();
        MusicaDAO mdao = new MusicaDAO();

        m.setNome(txtMusica.getText());

        Artista artista = (Artista) ComboBoxArtista.getSelectedItem();
        m.setIdartista(artista.getIdartista());

        Compositor compositor = (Compositor) ComboBoxCompositor.getSelectedItem();
        m.setIdcompositor(compositor.getIdcompositor());

        Album album = (Album) ComboBoxAlbum.getSelectedItem();
        m.setIdalbum(album.getId());

        m.setAudio(filename);

        mdao.create(m);
        fileSaver();

        limparCampos();
        listarMusica();
    }

    public void editarMusica() {
        //if (tblMusicas.getSelectedRow() != 1) {
        tblMusicas.getSelectedRow();
        Musica m = new Musica();
        MusicaDAO mdao = new MusicaDAO();
        m.setNome(txtMusica.getText());

        m.setIdartista(Integer.parseInt(txtIdArtista.getText()));

        if (ComboBoxArtista.getSelectedItem().equals("Selecione...")) {
            m.setIdartista(Integer.valueOf(txtIdArtista.getText()));

        } else {
            Artista artista = (Artista) ComboBoxArtista.getSelectedItem();
            m.setIdartista(artista.getIdartista());
        }

        if (ComboBoxAlbum.getSelectedItem().equals("Selecione...")) {
            m.setIdalbum(Integer.valueOf(txtIdAlbum.getText()));

        } else {
            Album album = (Album) ComboBoxAlbum.getSelectedItem();
            m.setIdalbum(album.getId());
        }

        if (ComboBoxCompositor.getSelectedItem().equals("Selecione...")) {
            m.setIdcompositor(Integer.valueOf(txtIdCompositor.getText()));

        } else {

            Compositor compositor = (Compositor) ComboBoxCompositor.getSelectedItem();
            m.setIdcompositor(Integer.valueOf(compositor.getIdcompositor()));
        }

        m.setIdmusica((int) tblMusicas.getValueAt(tblMusicas.getSelectedRow(), 0));
        mdao.update(m);
        limparCampos();
        listarMusica();
        populaComboBoxArtista();
        populaComboBoxCompositor();
        populaComboBoxAlbum();

    }

    public void deletarMusica() {
        String sql = "DELETE FROM musica WHERE idmusica = ?";

        try {
            pst = conecta.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtCodigo.getText()));
            pst.execute();

            JOptionPane.showMessageDialog(null, "Música deletada com sucesso!");
            limparCampos();
            listarMusica();

        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }

    public void limparCampos() {
        musicpath.setText(null);
        txtCodigo.setText(null);
        txtMusica.setText(null);
        populaComboBoxArtista();
        populaComboBoxCompositor();
        populaComboBoxAlbum();
        txtIdAlbum.setText(null);
        txtIdArtista.setText(null);
        txtIdCompositor.setText(null);

    }

    public void mostrarItens() {
        String sql = "SELECT idartista, idcompositor, idalbum FROM musica WHERE nome_musica LIKE ?";
        int seleciona = tblMusicas.getSelectedRow();
        try {
            pst = conecta.prepareStatement(sql);
            pst.setString(1, tblMusicas.getModel().getValueAt(seleciona, 1).toString() + "%");
            rs = pst.executeQuery();
            while (rs.next()) {
                Artista artista = new Artista();
                artista.setIdartista(Integer.parseInt(rs.getString("idartista")));

                txtIdArtista.setText(Integer.toString(artista.getIdartista()));

                Compositor compositor = new Compositor();
                compositor.setIdcompositor(Integer.parseInt(rs.getString("idcompositor")));
                txtIdCompositor.setText(Integer.toString(compositor.getIdcompositor()));

                Album album = new Album();
                album.setId(Integer.parseInt(rs.getString("idalbum")));
                txtIdAlbum.setText(Integer.toString(album.getId()));
            }

        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, error);
        }

        txtCodigo.setText(tblMusicas.getModel().getValueAt(seleciona, 0).toString());
        txtMusica.setText(tblMusicas.getModel().getValueAt(seleciona, 1).toString());

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtCodigo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ComboBoxCompositor = new javax.swing.JComboBox<>();
        txtMusica = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        btnCadastrar = new javax.swing.JButton();
        ComboBoxAlbum = new javax.swing.JComboBox<>();
        btnEditar = new javax.swing.JButton();
        btnDeletar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        txtPesquisar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMusicas = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        ComboBoxArtista = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtIdArtista = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtIdCompositor = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtIdAlbum = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        musicpath = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);

        txtCodigo.setBackground(new java.awt.Color(102, 102, 102));
        txtCodigo.setEnabled(false);

        jLabel7.setText("Compositor:");

        jLabel2.setText("Nome:");

        ComboBoxCompositor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel8.setText("Álbum:");

        btnCadastrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/edu/ifms/icons/adicionar-usuário-masculino-25.png"))); // NOI18N
        btnCadastrar.setText("Cadastrar");
        btnCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarActionPerformed(evt);
            }
        });

        ComboBoxAlbum.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/edu/ifms/icons/editar-usuário-masculino-25.png"))); // NOI18N
        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnDeletar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/edu/ifms/icons/remover-usuário-masculino-25.png"))); // NOI18N
        btnDeletar.setText("Deletar");
        btnDeletar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletarActionPerformed(evt);
            }
        });

        btnLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/edu/ifms/icons/apagador-25.png"))); // NOI18N
        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        txtPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarKeyReleased(evt);
            }
        });

        tblMusicas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblMusicas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMusicasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMusicas);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/edu/ifms/icons/pesquisar-25.png"))); // NOI18N
        jLabel5.setText("Buscar");

        ComboBoxArtista.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));

        jLabel1.setText("Código:");

        jLabel6.setText("Artista:");

        txtIdArtista.setEditable(false);
        txtIdArtista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdArtistaActionPerformed(evt);
            }
        });

        jLabel9.setText("ID Artista");

        txtIdCompositor.setEditable(false);
        txtIdCompositor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdCompositorActionPerformed(evt);
            }
        });

        jLabel10.setText("ID Compositor");

        txtIdAlbum.setEditable(false);
        txtIdAlbum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdAlbumActionPerformed(evt);
            }
        });

        jLabel11.setText("ID Álbum");

        jButton2.setText("Salvar Arquivo");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(3, 3, 3)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdArtista, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(40, 40, 40))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdCompositor, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtIdAlbum, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBoxArtista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBoxCompositor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(ComboBoxAlbum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(33, 33, 33))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnDeletar, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(42, 42, 42)
                                        .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMusica, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(musicpath, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2)))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtMusica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ComboBoxArtista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(ComboBoxCompositor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(ComboBoxAlbum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(txtIdArtista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtIdCompositor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtIdAlbum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(musicpath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCadastrar)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnDeletar)
                        .addComponent(btnEditar)
                        .addComponent(btnLimpar)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarActionPerformed
        if (txtMusica.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Informe o nome da música");
        } else {
            if (ComboBoxArtista.getModel().getSelectedItem() == "Selecione...") {
                JOptionPane.showMessageDialog(null, "Informe o Artista");
            } else {
                if (ComboBoxCompositor.getModel().getSelectedItem() == "Selecione...") {
                    JOptionPane.showMessageDialog(null, "Informe o Compositor");
                } else {
                    if (ComboBoxAlbum.getModel().getSelectedItem() == "Selecione...") {
                        JOptionPane.showMessageDialog(null, "Informe o Album");
                    } else {
                        try {
                            cadastrarMusica();
                        } catch (IOException ex) {
                            Logger.getLogger(formCadMusicas.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        limparCampos();
                        listarMusica();
                    }
                }
            }
        }

    }//GEN-LAST:event_btnCadastrarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        //  if (txtMusica.getText().isEmpty()) {
        //       JOptionPane.showMessageDialog(null, "Informe o nome da música");
        //   } else {
        editarMusica();
        //  }

    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnDeletarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletarActionPerformed
        if (txtCodigo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione uma música");
        } else {
            deletarMusica();
            limparCampos();
        }

    }//GEN-LAST:event_btnDeletarActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        limparCampos();
    }//GEN-LAST:event_btnLimparActionPerformed

    private void txtPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarKeyReleased
        pesquisarMusicas();
    }//GEN-LAST:event_txtPesquisarKeyReleased

    private void tblMusicasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMusicasMouseClicked
        populaComboBoxArtista();
        populaComboBoxCompositor();
        populaComboBoxAlbum();
        mostrarItens();
    }//GEN-LAST:event_tblMusicasMouseClicked

    private void txtIdArtistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdArtistaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdArtistaActionPerformed

    private void txtIdCompositorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdCompositorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdCompositorActionPerformed

    private void txtIdAlbumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdAlbumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdAlbumActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecione a música no format WAV");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Audio Wav", "wav");
        chooser.setFileFilter(filtro);

        chooser.showOpenDialog(null);
        File f = chooser.getSelectedFile();
        String filepath = f.getAbsolutePath();
        musicpath.setText(filepath);
        filename = f.getName();
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Object> ComboBoxAlbum;
    private javax.swing.JComboBox<Object> ComboBoxArtista;
    private javax.swing.JComboBox<Object> ComboBoxCompositor;
    private javax.swing.JButton btnCadastrar;
    private javax.swing.JButton btnDeletar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField musicpath;
    private javax.swing.JTable tblMusicas;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtIdAlbum;
    private javax.swing.JTextField txtIdArtista;
    private javax.swing.JTextField txtIdCompositor;
    private javax.swing.JTextField txtMusica;
    private javax.swing.JTextField txtPesquisar;
    // End of variables declaration//GEN-END:variables
}
